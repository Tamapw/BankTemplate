package ru.tama.banktemplate.account;

/**
 * Банковские(Корреспондетские) счета в определённом банке.
 *
 * @author tama
 */
public class BankAccount extends Account {

    private String name;

    public BankAccount(String name) {
        super("10000");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
