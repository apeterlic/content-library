package com.tier1app.livelock;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private BigDecimal balance;

    final Lock lock = new ReentrantLock();

    public Account(BigDecimal balance) {
        this.balance = balance;
    }

    public void deposit(BigDecimal amount) {
        this.balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        this.balance = balance.subtract(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "balance=" + balance +
                '}';
    }
}
