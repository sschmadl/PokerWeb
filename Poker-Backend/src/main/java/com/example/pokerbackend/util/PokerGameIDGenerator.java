package com.example.pokerbackend.util;

import java.util.UUID;

public class PokerGameIDGenerator {
    public static String generateID() {
        // Generate a random UUID and take the first 8 characters
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return id;
    }
}