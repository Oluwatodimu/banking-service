package com.woodcore.backend.bankingservice.utils;

import java.util.Random;

public class AccountNumberGenerator {

    public static String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            accountNumber.append(random.nextInt(10));
        }

        accountNumber.insert(0, random.nextInt(9) + 1);
        return accountNumber.toString();
    }
}
