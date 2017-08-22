package ru.tama.banktemplate;

import ru.tama.banktemplate.command.CommandReader;
import ru.tama.banktemplate.readers.StartedBankReader;

import java.io.File;

/**
 * Created by tama on 19.08.17.
 */
public class StartSolution {
    public static void main(String[] args) {
        File dirBank = new File("bank");
        if (!dirBank.exists()) {
            dirBank.mkdir();
        }

        File dirBanks = new File("bank" + File.separator + "banks");
        if (!dirBanks.exists()) {
            dirBanks.mkdir();
        }

        File dirStartedBanks = new File("bank" + File.separator + "started");
        if (!dirStartedBanks.exists()) {
            dirStartedBanks.mkdir();
        }

        Bank.loadBanks();

        System.out.println("Узнать информацию о доступных командах можно с помощью команды help.");

        StartedBankReader bankReader = new StartedBankReader();
        bankReader.setDaemon(true);
        bankReader.start();

        CommandReader reader = new CommandReader();
        reader.start();

    }
}
