package com.example.pokerbackend.util.commands;

public class JoinGameStatus {
    String command;
    private JoinGameStatus(String command) {
        this.command = command;
    }
    public static JoinGameStatus joinSuccess() {
        return new JoinGameStatus("join-success");
    }

    public static JoinGameStatus joinFailed() {
        return new JoinGameStatus("join-failed");
    }
}
