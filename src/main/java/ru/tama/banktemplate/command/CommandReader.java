package ru.tama.banktemplate.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Нить считывает команды с консоли и отдаёт их исполнение классу {@link CommandExecutor}
 *
 * @author tama
 */
public class CommandReader extends Thread {
    static CommandReader  reader;

    public CommandReader() {
        reader = this;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (!interrupted()) {
                System.out.println("---");
                String[] input = reader.readLine().split(" ");

                try {
                    Thread executor = new Thread(new CommandExecutor(input));
                    executor.start();
                    executor.join();

                sleep(100);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
