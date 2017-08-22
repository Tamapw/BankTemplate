package ru.tama.banktemplate.readers;

import ru.tama.banktemplate.Bank;
import ru.tama.banktemplate.PaymentDocument;

import java.io.*;

/**
 * Нить проверяет папку запросов в конкретном банке и в случае появления запроса - исполняет его.
 *
 * @author tama
 */
public class TransferRequestReader extends Thread {
    @Override
    public void run() {
        while (!interrupted()) {
            File requestDir = new File("bank" + File.separator + Bank.bank.getName() + File.separator + "PaymentDocuments" + File.separator + "Request");
            String dirRequest = "bank" + File.separator + Bank.bank.getName() + File.separator + "PaymentDocuments" + File.separator + "Request" + File.separator;

            File[] dirs = requestDir.listFiles();

            if (dirs.length != 0) {
                for (File dir : dirs) {
                    //Новый файл - сериализованный платежный документ с информацией о платеже.
                    PaymentDocument document = (PaymentDocument) readObj(dirRequest + dir.getName());
                    //Т.к. платёжный документ был составлен не в нашем банке, а получен из вне,
                    // то isOurDocument метода transfer выставляется в false.
                    Bank.bank.transfer(document, false);

                    dir.delete();
                }
            }

            try {
                sleep(500);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    private Object readObj(String path) {
        try (ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)))) {
            return input.readObject();
        } catch (IOException  | ClassNotFoundException ex) {
        }

        return null;
    }
}
