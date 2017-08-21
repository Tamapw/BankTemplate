package ru.tama.banktemplate;

import java.io.Serializable;

/**
 * Платежный документ. При создании нового документа его id автоинкрементируется.
 *
 * @author tama
 */
public class PaymentDocument implements Serializable {
    /**
     * Количество документов в банке.
     */
    private static long amountDocument = 0;

    private long id;

    /**
     * Счёт, с которого совершается перевод. <br>
     * Формат счёта: "<i>id</i>-<i>code</i>", где <br>
     * &ensp;  <i>id</i> - уникальный номер счёта в банке. <br>
     * &ensp;  <i>code</i> - номер банка в системе.<br>
     * example: 123-2
     */
    private String from;

    /**
     * Счёт, на который совершается перевод. <br>
     * Формат счёта: "<i>id</i>-<i>code</i>", где<br>
     * &ensp;  <i>id</i> - уникальный номер счёта в банке.<br>
     * &ensp;  <i>code</i> - номер банка в системе.<br>
     * example: 123-2
     */
    private String to;

    /**
     * Количество денег, которые нужно перевести.
     * example: 1523
     */
    private String amount;

    /**
     * Определяет, успешно ли прошла операция перевода денег. <br>
     * true, если успешно <br>
     * false, если не успешно. <br>
     */
    private boolean isSuccessfulOperation;

    /**
     * Описывает ошибку, из-за которой проведение операции было провалено. <br>
     * example: Некорректно указан счёт с которого совершается операция <br>
     */
    private String description;

    /**
     * Определяет, завершился ли перевод средств. <br>
     * true, если перевод завершён <br>
     * false, если перевод всё ещё совершается <br>
     */
    private boolean isOperationProcessEnd;

    /**
     * Определяет, направлен документ от нашего банка или от третьего банка. <br>
     * true, когда перевод был инициирован банком, который не имеет никакого отношения к данному документу. <br>
     * false, когда перевод был инициирован одним из банков, счета которых указаны в документе.
     */
    private boolean isAnotherBank;

    public void setAnotherBank(boolean anotherBank) {
        isAnotherBank = anotherBank;
    }

    public boolean isAnotherBank() {
        return isAnotherBank;
    }

    private PaymentDocument() {
        this.id = ++amountDocument;
    }

    public PaymentDocument(String from, String to, String amount) {
        this();
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public PaymentDocument(PaymentDocument document) {
        this();
        this.from = document.from;
        this.to = document.to;
        this.amount = document.amount;
        this.isSuccessfulOperation = document.isSuccessfulOperation;
        this.isOperationProcessEnd = document.isOperationProcessEnd;
        this.description = document.description;
        this.isAnotherBank = document.isAnotherBank;
    }

    public long getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSuccessfulOperation() {
        return isSuccessfulOperation;
    }

    public void setSuccessfulOperation(boolean isSuccessful) {
        this.isSuccessfulOperation = isSuccessful;
    }

    public static void setAmountDocument(long amountDocument) {
        PaymentDocument.amountDocument = amountDocument;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isOperationProcessEnd() {
        return isOperationProcessEnd;
    }

    public void setOperationProcessEnd(boolean operationProcessEnd) {
        this.isOperationProcessEnd = operationProcessEnd;
    }
}