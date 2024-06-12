package com.tier1app.livelock;

import java.math.BigDecimal;
import java.util.Random;

public class LivelockSolution {

    private static final Random random = new Random();

    public static void main(String[] args) {
        Account account1 = new Account(BigDecimal.valueOf(1000));
        Account account2 = new Account(BigDecimal.valueOf(1000));

        new Thread(() -> transferFunds(account1, account2, BigDecimal.valueOf(200))).start();
        new Thread(() -> transferFunds(account2, account1, BigDecimal.valueOf(100))).start();
    }

    private static void transferFunds(Account fromAccount, Account toAccount, BigDecimal amount) {
        while (true) {
            if (fromAccount.lock.tryLock()) {
                System.out.println(Thread.currentThread()
                    .getName() + ": fromAccount lock acquired, acquiring lock to toAccount");

                sleep(50);

                try {
                    if (toAccount.lock.tryLock()) {
                        try {
                            System.out.println(Thread.currentThread()
                                .getName() + ": toAccount lock acquired");
                            transfer(fromAccount, toAccount, amount);
                            break;
                        } finally {
                            System.out.println(Thread.currentThread()
                                .getName() + ": releasing lock on toAccount");
                            toAccount.lock.unlock();
                        }
                    }
                } finally {
                    System.out.println(Thread.currentThread()
                        .getName() + ": cannot acquire lock on toAccount, releasing lock on fromAccount");
                    fromAccount.lock.unlock();
                }
            } else {
                System.out.println(Thread.currentThread()
                    .getName() + ": cannot acquire lock on fromAccount");
            }

            sleep(randomWait());
        }
    }

    public static void transfer(final Account fromAccount, final Account toAccount, final BigDecimal amount) {
        if (fromAccount.getBalance()
            .compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        } else {
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            System.out.println(Thread.currentThread()
                .getName() + " transferred $" + amount + " from " + fromAccount + " to " + toAccount);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int randomWait() {
        return random.nextInt(20, 100);
    }

}
