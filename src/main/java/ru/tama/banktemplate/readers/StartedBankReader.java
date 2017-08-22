package ru.tama.banktemplate.readers;

import ru.tama.banktemplate.Bank;

import java.io.File;

/**
 * Нить проверяет, устарел ли список запущенных банков и обновляет его.
 */
public class StartedBankReader extends Thread {
    @Override
    public void run() {
        while (!interrupted()) {
            File bank = new File("bank" + File.separator + "started" + File.separator);

            File[] dirs = bank.listFiles();

            if (dirs != null && dirs.length != Bank.startedBanks.size()) {
                Bank.loadStartedBanks();
            }

            try {
                sleep(500);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
}
