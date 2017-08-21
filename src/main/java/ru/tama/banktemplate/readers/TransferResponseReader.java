package ru.tama.banktemplate.readers;

import ru.tama.banktemplate.Bank;
import ru.tama.banktemplate.PaymentDocument;

import java.io.*;
import java.util.Optional;

/**
 * Нить проверяет папку ответов в конкретном банке и в случае появления ответа на запрос - изменяет соответствующий запросу платёжный документ.
 *
 * @author tama
 */
public class TransferResponseReader extends Thread {
    @Override
    public void run() {
        while (!interrupted()) {
            File responseDir = new File("bank/" + Bank.bank.getName() + "/PaymentDocuments/Response");
            String dirResponse = "bank/" + Bank.bank.getName() + "/PaymentDocuments/Response/";

            File[] dirs = responseDir.listFiles();

            if (dirs.length != 0) {
                for (File dir : dirs) {
                    //Файл соответствует сериализированному документу из запроса, в который другой банк внёс соответствующие изменения.
                    PaymentDocument newDocument = (PaymentDocument) readObj(dirResponse + dir.getName());

                    //Ищем документ из ответа в банке
                    Optional<PaymentDocument> documentOptional = Bank.bank.getPaymentDocuments()
                            .stream()
                            .filter(doc -> doc.getId() == newDocument.getId())
                            .findFirst();

                    //Если не находим - удаляем файл.
                    if (!documentOptional.isPresent()) {
                        dir.delete();
                        return;
                    }

                    //Иначе вносим соответствующие изменения в документ.
                    PaymentDocument document = documentOptional.get();
                    document.setSuccessfulOperation(newDocument.isSuccessfulOperation());
                    document.setOperationProcessEnd(newDocument.isOperationProcessEnd());
                    document.setDescription(newDocument.getDescription());

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
