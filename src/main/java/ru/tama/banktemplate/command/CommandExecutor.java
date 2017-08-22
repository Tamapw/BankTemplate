package ru.tama.banktemplate.command;

import ru.tama.banktemplate.Bank;
import ru.tama.banktemplate.PaymentDocument;
import ru.tama.banktemplate.account.PersonalAccount;

/**
 * Исполняет команды, полученные от класса {@link CommandReader}. Для определения команды используется {@link CommandType}.
 *
 *
 * @author tama
 */
public class CommandExecutor implements Runnable {
    private String[] args;

    public void run() {
        CommandType commandType;
        try {
            commandType = CommandType.parse(args);
        } catch (IllegalArgumentException ex) {
            System.out.printf(ex.getMessage() + "\n");
            return;
        }

        switch (commandType) {
            case COMMAND_START: {
                startBank();
            } break;

            case COMMAND_STOP: {
                stopBank();
            } break;

            case COMMAND_CREATE: {
                createBank();
            } break;

            case COMMAND_TRANSFER: {
                transfer();
            } break;

            case COMMAND_PRINT_ACCOUNTS: {
                printAccounts();
            } break;

            case COMMAND_PRINT_BANKS: {
                printBanks();
            } break;

            case COMMAND_EXIT: {
                exit();
            }

            case COMMAND_ADD_ACCOUNTS: {
                addAccounts();
            }

            case COMMAND_ADD_ACCOUNT_WITH_MONEY: {
                addAccountWithMoney();
            } break;

            case COMMAND_HELP: {
                help();
            }
        }
    }

    CommandExecutor(String... args) {
        this.args = args;
    }

    /**
     * Запускает банк в данном экземпляре приложения. <br>
     * Аргументы команды должны соответствовать команде {@link CommandType#COMMAND_START}
     */
    private void startBank() {
        if (args.length != 2) {
            System.out.println("Некорректное количество аргументов.");
            return;
        }

        String name = args[1];
        if (!Bank.banks.containsValue(name)) {
            System.out.println("Банка с таким именем не существует.");
            return;
        }

        if (Bank.bank != null) {
            System.out.println("На этом экцемпляре приложения банк уже запущен.");
            return;
        }

        if (Bank.startedBanks.contains(name)) {
            System.out.println("Банк с таким именем уже запущен в системе.");
            return;
        }

        System.out.println("Банк запускается...");
        Bank.bank = new Bank(name);
        Bank.bank.start();
        System.out.println("Банк открыт.");
    }

    /**
     * Останавливает запущенный банк на этом экземпляре приложения. <br>
     * Аргументы команды должны соответствовать команде {@link CommandType#COMMAND_STOP}
     */
    private void stopBank() {
        if (args.length != 1) {
            System.out.println("Некорректное количество аргументов.");
            return;
        }

        if (Bank.bank == null) {
            System.out.println("На этом экземпляре приложения никакой банк не запущен.");
            return;
        }

        Bank.bank.interrupt();
        System.out.println("Банк закрывается...");
        while (Bank.bank != null) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {

            }
        }
        System.out.println("Банк закрыт.");
    }

    /**
     * Создаёт банк в системе. <br>
     * Аргументы команды должны соответствовать команде {@link CommandType#COMMAND_CREATE}
     */
    private void createBank() {
        if (args.length != 2) {
            System.out.println("Некорректное количество аргументов.");
            return;
        }

        if (Bank.createBank(args[1])) {
            System.out.println("Создано.");
        }
    }

    /**
     * Совершает перевод средств с счёта на счёт. <br>
     * Аргументы команды должны соответствовать команде {@link CommandType#COMMAND_TRANSFER}
     */
    private void transfer() {
        if (args.length != 4) {
            System.out.println("Некорректное количество аргументов.");
            return;
        }

        if (Bank.bank == null) {
            System.out.println("На этом экземпляре приложения никакой банк не запущен.");
            return;
        }

        //Счёт, с которого совершается перевод
        String from = args[1];
        //Счёт, на который совершается перевод
        String to = args[2];
        //Количество денег, которые нужно перевести
        String amount = args[3];

        //Если первый счёт не в нашем банке
        if (!Bank.banks.get(args[1].split("-")[1]).equals(Bank.bank.getName())) {
            //И второй счёт тоже не в нашем банке
            if (!Bank.banks.get(args[2].split("-")[1]).equals(Bank.bank.getName())) {
                //Совершаем перевод
                Bank.bank.transfer(new PaymentDocument(from, to, amount), true);
                return;
            }

            //Если только первый счёт не в нашем банке. Такой платёж совершить не возможно - мы не можем запросить снятие средств с счета другого банка
            System.out.println("Невозможно исполнить такой платёж.");
            return;
        }

        Bank.bank.transfer(new PaymentDocument(from, to, amount), true);

    }

    /**
     * Выводит на экран счета банка. <br>
     * Аргументы команды должны соответствовать команде {@link CommandType#COMMAND_PRINT_ACCOUNTS}
     */
    private void printAccounts() {
        if (args.length != 2) {
            System.out.println("Некорректное количество аргументов.");
            return;
        }

        if (Bank.bank == null) {
            System.out.println("На этом экземпляре не запущен банк.");
            return;
        }

        Bank.bank.print();
    }

    /**
     * Выводит на экран список всех банков. <br>
     * Аргументы команды должны соответствовать команде {@link CommandType#COMMAND_PRINT_BANKS}
     */
    private void printBanks() {
        if (args.length != 2) {
            System.out.println("Некорректное количество аргументов.");
            return;
        }

        Bank.banks.forEach((key, value) -> System.out.printf(
                "code: %s, name: %s | %s\n", key, value, Bank.startedBanks.contains(value) ? "ONLINE" : "OFFLINE"
        ));
    }

    /**
     * Закрывает приложение. <br>
     * Аргументы команды должны соответствовать команде {@link CommandType#COMMAND_EXIT}
     */
    private void exit() {
        if (args.length != 1) {
            System.out.println("Некорректное количество аргументов.");
            return;
        }

        if (Bank.bank != null) {
            Bank.bank.interrupt();
            while (Bank.bank != null) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {

                }
            }
        }

        CommandReader.reader.interrupt();
    }

    /**
     * Добавляет пользовательские счета в банк. <br>
     * Аргументы команды должны соответствовать команде {@link CommandType#COMMAND_ADD_ACCOUNTS}
     */
    private void addAccounts() {
        if (args.length != 3) {
            System.out.println("Некорректное количество аргументов.");
            return;
        }

        if (Bank.bank == null) {
            System.out.println("На этом экземпляре не запущен банк.");
            return;
        }

        int count = Integer.parseInt(args[2]);
        for (int i = 0; i < count; i++) {
            PersonalAccount account = new PersonalAccount(String.valueOf((i + 1) * 1000));
            Bank.bank.getPersonalAccounts().add(account);
            Bank.bank.getAllAccount().add(account);
        }
        System.out.println("Добавлено.");
    }

    /**
     * Добавляет пользовательский счёт в банк с определённым количеством денег. <br>
     * Аргументы команды должны соответствовать команде {@link CommandType#COMMAND_ADD_ACCOUNT_WITH_MONEY}
     */
    private void addAccountWithMoney() {
        if (args.length != 3) {
            System.out.println("Некорректное количество аргументов.");
            return;
        }

        if (Bank.bank == null) {
            System.out.println("На этом экземпляре не запущен банк.");
            return;
        }

        String money = args[2];

        try {
            double correctMoney = Double.parseDouble(money);
            if (correctMoney < 0) {
                System.out.printf("Сумма денег не может быть отрицательной.");
            }
        } catch (NumberFormatException ex) {
            System.out.printf("Некорректно введена сумма денег.");
            return;
        }

        PersonalAccount account = new PersonalAccount(money);
        Bank.bank.getPersonalAccounts().add(account);
        Bank.bank.getAllAccount().add(account);

        System.out.println("Добавлено.");
    }

    /**
     * Выводит на экран все имеющиеся команды с их аргументами.
     */
    private void help() {
        if (args.length != 1) {
            System.out.printf("Некорректно введена команда");
            return;
        }

        for (CommandType commandType : CommandType.values()) {
            System.out.println(commandType);
        }
    }
}
