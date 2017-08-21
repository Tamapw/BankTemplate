package ru.tama.banktemplate.readers;

import ru.tama.banktemplate.Bank;
import ru.tama.banktemplate.account.BankAccount;

import java.io.File;

/**
 * Нить проверяет папку конкретного банка и в случае появления повых файлов,
 * которые означают создание нового банка в системе - вносит изменения и добавляет счёт нового банка в существующий банк
 *
 * @author tama
 */
public class NewBankReader extends Thread {
    @Override
    public void run() {
        while (!interrupted()) {
            File bank = new File("bank/" + Bank.bank.getName() + "/");

            File[] dirs = bank.listFiles();

            if (dirs.length != 3) {
                for (File dir : dirs) {
                    if (!dir.isDirectory()) {
                        //новый файл типа 1-sbt, где 1 - номер банка в системе, sbt - имя банка в системе.
                        String[] parts = dir.getName().split("-");

                        if (parts.length == 2) {
                            String code = parts[0];
                            String name = parts[1];
                            Bank.banks.put(code, name);

                            BankAccount account = new BankAccount(name);

                            Bank.bank.getBankAccounts().add(account);
                            Bank.bank.getAllAccount().add(account);
                        }

                        dir.delete();
                    }
                }
            }

            try {
                sleep(500);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
}
