package ru.tama.banktemplate;

import ru.tama.banktemplate.command.CommandReader;

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

        Bank.loadBanks();

        CommandReader reader = new CommandReader();
        reader.start();
    }
}
