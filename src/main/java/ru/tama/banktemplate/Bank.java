package ru.tama.banktemplate;

import ru.tama.banktemplate.account.Account;
import ru.tama.banktemplate.account.BankAccount;
import ru.tama.banktemplate.account.PersonalAccount;
import ru.tama.banktemplate.readers.NewBankReader;
import ru.tama.banktemplate.readers.TransferRequestReader;
import ru.tama.banktemplate.readers.TransferResponseReader;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Класс представляет собой банк и методы по управлению банком.
 *
 * @author tama
 */
public class Bank extends Thread {
    /**
     * banks представляет собой пару "номер-ИмяБанка" всех банков, существующих в системе. <br>
     * <li>1-sbt, где 1 - номер банка, sbt - имя банка.</li>
     * <li>2-sb, где 2 - номер банка, sb - имя банка</li>
     */
    public static HashMap<String, String> banks = new HashMap<>();
    /**
     * Запущенный конкретный банк в экземпляре приложения.
     */
    public volatile static Bank bank;

    /**
     * Имя запущенного банка.
     */
    private String name;

    /**
     * Все счета конкретного банка. И клиентские, и корреспондетские.
     */
    private List<Account> allAccount = new ArrayList<>();
    /**
     * Только клиентские счета конкретного банка.
     */
    private List<PersonalAccount> personalAccounts = new ArrayList<>();
    /**
     * Только банковские счета конкретного банка.
     */
    private List<BankAccount> bankAccounts = new ArrayList<>();
    /**
     * Платежные документы конкретного банка.
     */
    private List<PaymentDocument> paymentDocuments = new ArrayList<>();

    public List<PaymentDocument> getPaymentDocuments() {
        return paymentDocuments;
    }

    public void setPaymentDocuments(List<PaymentDocument> paymentDocuments) {
        this.paymentDocuments = paymentDocuments;
    }

    public List<Account> getAllAccount() {
        return allAccount;
    }

    public List<PersonalAccount> getPersonalAccounts() {
        return personalAccounts;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    /**
     * При запуске банка загружает все данные с папки банка(если таковых не оказалось - заполняет данные сам) и запускает ридеры для считывания и обработки информации. <br>
     * Затем ожидает остановки банка и сохраняет всю информацию, полученную за сеанск в папку банка.
     */
    @Override
    public void run() {
        load();

        if (bankAccounts.size() == 0) {
            banks.values().forEach(name -> {
                BankAccount account = new BankAccount(name);
                bankAccounts.add(account);
                allAccount.add(account);
            });
        }

        NewBankReader newBankReader = new NewBankReader();
        newBankReader.start();

        TransferRequestReader transferRequestReader = new TransferRequestReader();
        transferRequestReader.start();

        TransferResponseReader transferResponseReader = new TransferResponseReader();
        transferResponseReader.start();

        while (!interrupted()) {
            try {
                sleep(300);
            } catch (InterruptedException ex) {
                break;
            }
        }

        transferRequestReader.interrupt();
        transferResponseReader.interrupt();
        newBankReader.interrupt();
        save();
        bank = null;
    }

    public Bank(String name) {
        super(name);
        this.name = name;
    }

    /**
     * Загружает все банки системы в карту {@link Bank#banks}
     */
    public static void loadBanks() {
        File dir = new File("bank/banks/");

        String[] banksName = dir.list();

        for (String s : banksName) {
            String[] partsName = s.split("-");
            if (partsName.length != 2) {

            }

            String code = partsName[0];
            String name = partsName[1];

            banks.put(code, name);
        }
    }

    /**
     * Загружает данные о конкретных счетах банка из папки банка.
     */
    private void load() {
        String dirPersonalAccounts = String.format(
                "bank/%s/PersonalAccounts/accounts.ser",
                name
        );
        String dirBankAccounts = String.format(
                "bank/%s/BankAccounts/accounts.ser",
                name
        );
        String dirPaymentDocuments = String.format(
                "bank/%s/PaymentDocuments/documents.ser",
                name
        );


        try {
            bankAccounts = (List<BankAccount>) readObj(dirBankAccounts);
            personalAccounts = (List<PersonalAccount>) readObj(dirPersonalAccounts);
            paymentDocuments = (List<PaymentDocument>) readObj(dirPaymentDocuments);
        } catch (Exception ex) {
            bankAccounts = new ArrayList<>();
            personalAccounts = new ArrayList<>();
            paymentDocuments = new ArrayList<>();
        }

        allAccount.addAll(bankAccounts);
        allAccount.addAll(personalAccounts);
        Account.setAccountCounter(allAccount.size());
        PaymentDocument.setAmountDocument(paymentDocuments.size());
    }

    /**
     * Сохраняет все данные банка в папку банка.
     */
    private void save() {
        String dirPersonalAccounts = String.format(
                "bank/%s/PersonalAccounts/accounts.ser",
                name
        );
        String dirBankAccounts = String.format(
                "bank/%s/BankAccounts/accounts.ser",
                name
        );
        String dirPaymentDocuments = String.format(
                "bank/%s/PaymentDocuments/documents.ser",
                name
        );

        writeObj(dirPersonalAccounts, personalAccounts);
        writeObj(dirBankAccounts, bankAccounts);
        writeObj(dirPaymentDocuments, paymentDocuments);
    }

    /**
     * Создаёт банк и все соответствующие банку папки в системе.
     *
     * @param name имя банка, который необходимо создать.
     */
    public static void createBank(String name) {
        loadBanks();

        if (banks.containsValue(name)) {
            System.out.println("Банк с таким именем уже существует.");
            return;
        }

        try {
            //Номер нового банка, который необходимо присвоить.
            long newBankCode = 1;

            if (banks.size() != 0) {
                newBankCode = banks.keySet()
                        .stream()
                        .mapToLong(Long::valueOf)
                        .max()
                        .getAsLong() + 1;
            }

            String[] allBanks = new File("bank/").list();

            //Оповещает каждый существующий банк о создании нового банка и вносит новый банк в папку banks.
            for (String bankName : allBanks) {
                File newFile = new File("bank/" + bankName + "/", newBankCode + "-" + name);
                newFile.createNewFile();
            }

            //Добавляет новый банк в карту banks.
            banks.put(String.valueOf(newBankCode), name);

            //Создаёт все нужные папки для нового банка.
            File newDir = new File("bank/" + name);
            File personalAccounts = new File("bank/" + name + "/", "PersonalAccounts");
            File bankAccounts = new File("bank/" + name + "/", "BankAccounts");
            File paymentDocuments = new File("bank/" + name + "/", "PaymentDocuments");
            File paymentDocumentsRequest = new File("bank/" + name + "/", "PaymentDocuments/Request");
            File paymentDocumentsResponse = new File("bank/" + name + "/", "PaymentDocuments/Response");

            newDir.mkdir();
            personalAccounts.mkdir();
            bankAccounts.mkdir();
            paymentDocuments.mkdir();
            paymentDocumentsRequest.mkdir();
            paymentDocumentsResponse.mkdir();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Проверяет счета документа на наличие в нашем банке и исполняет платёж.
     *
     * @param document - платёжный документ, перевод по которому необходимо совершить.
     * @param isOurDocument - проверка, создан ли документ в нашем банке или пришёл от другого банка. <br>
     *                 &ensp; true - документ создан в нашем банке <br>
     *                 &ensp; false - документ создан не в нашем банке.
     * @return true, если платёж был выполнен успешно, false иначе.
     */
    public boolean transfer(PaymentDocument document, boolean isOurDocument) {
        if (isOurDocument) {
            paymentDocuments.add(document);
        } else {
            paymentDocuments.add(new PaymentDocument(document));
        }

        //Счет вида id-num. 412-12, где 412 - номер лицевого счета в банке 12
        String bankCodeFrom = document.getFrom().split("-")[1];
        String bankCodeTo = document.getTo().split("-")[1];
        //Находятся ли счета в нашем банке.
        boolean fromIsOurBank = banks.get(bankCodeFrom).equals(name);
        boolean toIsOurBank = banks.get(bankCodeTo).equals(name);

        String idFrom = document.getFrom().split("-")[0];
        String idTo = document.getTo().split("-")[0];

        //Если оба счёта в нашем банке - исполняем перевод.
        if (fromIsOurBank && toIsOurBank) {
            return transferInOurBank(document);
        }

        //Если только первый счёт не в нашем банке, т.е. пришёл запрос.
        if (!fromIsOurBank && toIsOurBank) {
            return transferFromAnotherBank(document);
        }

        //Если только второй счёт не в нашем банке, т.е. отправляем запрос.
        if (fromIsOurBank && !toIsOurBank) {
            return transferToAnotherBank(document);
        }

        //Если оба счёта не в нашем банке - пересылаем документ в нужный банк.
        return transferInAnotherBanks(document);
    }

    /**
     * Выполняет перевод средств между счетами в нашем банке.
     *
     * @param document - платежный документ, в котором находится информация по платежу.
     * @return
     * @return true, если платёж был выполнен успешно, false иначе.
     */
    private boolean transferInOurBank(PaymentDocument document) {
        //Ищет аккаунт, с которого необходимо снять деньги.
        Optional<Account> accountFrom = allAccount
                    .stream()
                    .filter(account -> account.getId() == Long.parseLong(document.getFrom().split("-")[0]))
                    .findFirst();
        if (!accountFrom.isPresent()) {
            System.out.println("Некорректно введён счёт, с которого производится оплата.");
            return false;
        }

        //Ищет аккаунт, который необходимо пополнить.
        Optional<Account> accountTo = allAccount
                .stream()
                .filter(account -> account.getId() == Long.parseLong(document.getTo().split("-")[0]))
                .findFirst();
        if (!accountTo.isPresent()) {
            System.out.println("Некорректно введён счёт, на который производится оплата.");
            return false;
        }

        if (!accountFrom.get().takeMoney(new BigDecimal(document.getAmount()))) {
            System.out.println("Невозможно произвести операцию - на счету не хватает средств.");
            return false;
        }

        accountTo.get().addMoney(new BigDecimal(document.getAmount()));
        document.setSuccessfulOperation(true);
        document.setOperationProcessEnd(true);
        return true;
    }

    /**
     * Получает запрос от другого банка, вносит соответствующие изменения в счета и отправляет ответ о проведении платежа.
     *
     * @param document - платежный документ, который содержит информаци по платежу
     * @return true, если платёж был выполнен успешно, false иначе.
     */
    private boolean transferFromAnotherBank(PaymentDocument document) {
        //счёт в документе хранится в виде "id-code". "123-2" означает - лицевой счёт 123 в банке под номером 2.
        String bankName = banks.get(document.getFrom().split("-")[1]);
        String dirResponse = "bank/" + bankName + "/PaymentDocuments/Response/" + document.getFrom() + ":" + document.getTo() + ":" + document.getAmount();
        //Ищет аккаунт банка, деньги с которого необходимо снять.
        Optional<BankAccount> bankAccount = bankAccounts
                .stream()
                .filter(account -> account.getName().equals(bankName))
                .findFirst();
        //Ищет аккаунт пользователя, счёт которого необходимо пополнить.
        Optional<Account> accountTo = allAccount
                    .stream()
                    .filter(account -> account.getId() == Long.parseLong(document.getTo().split("-")[0]))
                    .findFirst();

        if (!bankAccount.isPresent()) {
            document.setOperationProcessEnd(true);
            document.setSuccessfulOperation(false);
            document.setDescription("Некорректно указан счёт, с которого производится оплата.");

            writeObj(dirResponse, document);
            return false;
        }

        if (!accountTo.isPresent()) {
            document.setOperationProcessEnd(true);
            document.setSuccessfulOperation(false);
            document.setDescription("Некорректно введён счёт, на который производится оплата.");

            writeObj(dirResponse, document);
            return false;
        }

        if (!bankAccount.get().takeMoney(new BigDecimal(document.getAmount()))) {
            document.setOperationProcessEnd(true);
            document.setSuccessfulOperation(false);
            document.setDescription("Недостаточная сумма на аккаунте банка.");

            writeObj(dirResponse, document);
            return false;
        }

        accountTo.get().addMoney(new BigDecimal(document.getAmount()));
        document.setSuccessfulOperation(true);
        document.setOperationProcessEnd(true);

        writeObj(dirResponse, document);
        return true;
    }

    /**
     * Снимает деньги с счёта нашего банка, заносит их в кореспондентский счёт банка, в который совершается перевод и отправляет запрос о платеже в другой банка. <br>
     * В случае, если документ был создан нашим банком(а не третим банком, который перенаправил документ), то ожидает ответ на запрос.
     *
     * @param document - платежный документ, который содержит информацию по платежу.
     * @return true, если платёж был выполнен успешно, false иначе.
     */
    private boolean transferToAnotherBank(PaymentDocument document) {
        //счёт в документе хранится в виде "id-code". "123-2" означает - лицевой счёт 123 в банке под номером 2.
        String bankName = banks.get(document.getTo().split("-")[1]);
        String dirRequest = "bank/" + bankName + "/PaymentDocuments/Request/" + document.getFrom() + ":" + document.getTo() + ":" + document.getAmount();
        //Ищет аккаунт банка, счет которого необходимо пополнить.
        Optional<BankAccount> bankAccount = bankAccounts
                .stream()
                .filter(account -> account.getName().equals(bankName))
                .findFirst();

        //Ищет аккаунт клиента, деньги с счёта которого необходимо снять.
        Optional<Account> accountFrom = allAccount
                    .stream()
                    .filter(account -> account.getId() == Long.parseLong(document.getFrom().split("-")[0]))
                    .findFirst();

        if (!bankAccount.isPresent()) {
            document.setOperationProcessEnd(true);
            document.setSuccessfulOperation(false);
            document.setDescription("Некорректно указан счёт, на который производится оплата.");

            return false;
        }

        if (!accountFrom.isPresent()) {
            document.setOperationProcessEnd(true);
            document.setSuccessfulOperation(false);
            document.setDescription("Некорректно введён счёт, с которого совершается оплата..");

            return false;
        }

        if (!accountFrom.get().takeMoney(new BigDecimal(document.getAmount()))) {
            document.setOperationProcessEnd(true);
            document.setSuccessfulOperation(false);
            document.setDescription("Нехватка суммы на счёту, с которого производится оплата.");

            return false;
        }

        bankAccount.get().addMoney(new BigDecimal(document.getAmount()));
        document.setOperationProcessEnd(false);
        document.setSuccessfulOperation(false);

        //Отправляем запрос
        writeObj(dirRequest, document);

        if (!document.isAnotherBank()) {
            //Ждёт ответ
            while (!document.isOperationProcessEnd()) {
                try {
                    sleep(100);
                } catch (InterruptedException ex) {

                }
            }

            //Откатывает изменения в случае провала перевода.
            if (!document.isSuccessfulOperation()) {
                bankAccount.get().takeMoney(new BigDecimal(document.getAmount()));
                accountFrom.get().addMoney(new BigDecimal(document.getAmount()));
            }
        }

        return document.isSuccessfulOperation();
    }

    /**
     * Отправляет платёжный документ на исполнение в другой банк. Оба счёта находятся не в нашем банке.
     *
     * @param document - документ, платёж по которому необходимо выполнить.
     * @return true, если платёж был передан в другой успешно, false иначе.
     */
    private boolean transferInAnotherBanks(PaymentDocument document) {
        String bankName = banks.get(document.getFrom().split("-")[1]);
        String dirRequest = "bank/" + bankName + "/PaymentDocuments/Request/" + document.getFrom() + ":" + document.getTo() + ":" + document.getAmount();
        document.setAnotherBank(true);
        writeObj(dirRequest, document);
        return true;
    }

    /**
     * Выводит на экран все счета конкретного банка. Вначале выводятся клиентские счета, затем корреспондентские.
     */
    public void print() {
        personalAccounts.forEach(System.out::println);
        System.out.println();
        bankAccounts.forEach(System.out::println);
    }

    private Object readObj(String path) {
        try (ObjectInput input = new ObjectInputStream (new BufferedInputStream(new FileInputStream(path)))) {
            return input.readObject();
        } catch (IOException  | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void writeObj(String path, Object obj) {
        try (ObjectOutput output = new ObjectOutputStream (new BufferedOutputStream(new FileOutputStream(path)))) {
            output.writeObject(obj);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}