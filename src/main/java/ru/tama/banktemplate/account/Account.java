package ru.tama.banktemplate.account;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Базовый класс счета, который реализует снятие денег с счёта и пополнение счета, а так же гарантирует автоинкремент id при создании потомков.
 *
 * @author tama
 */
public abstract class Account implements Serializable {
    /**
     * Количество уникальных счетов в банке.
     */
    public static long accountCounter;
    /**
     * Уникальный номер счёта в банке.
     */
    private long id;
    /**
     * Количество денег на счёте. Не должен уходить в минус.
     */
    private BigDecimal money;

    private Account() {
        accountCounter++;
        id = accountCounter;
    }

    public Account(String money) {
        this();
        this.money = new BigDecimal(money);
    }

    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    protected void setMoney(BigDecimal money) {
        this.money = money;
    }

    public static void setAccountCounter(long accountCounter) {
        Account.accountCounter = accountCounter;
    }

    /**
     * Снятие денег с счёта. Если при снятии счёт уходит в минус - операция не выполняется.
     *
     * @param money количество денег, которое необходимо снять с счёта.
     * @return true, если операция выполнена успешна, false иначе.
     */
    public boolean takeMoney(BigDecimal money) {
        if (!(this.money.subtract(money).signum() < 0)) {
            this.money = this.money.subtract(money);
            return true;
        }

        return false;
    }

    /**
     * Пополнение счёта.
     *
     * @param money количество денег, на которое необходимо пополнить счёт.
     * @return true. если операция выполнена успешна, false иначе.
     */
    public boolean addMoney(BigDecimal money) {
        this.money = this.money.add(money);

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != account.id) return false;
        return money != null ? money.equals(account.money) : account.money == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (money != null ? money.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", money=" + money +
                '}';
    }
}
