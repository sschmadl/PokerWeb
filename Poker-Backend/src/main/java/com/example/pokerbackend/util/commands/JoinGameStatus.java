package com.example.pokerbackend.util.commands;

public class JoinGameStatus {
    String command;
    String message;

    private JoinGameStatus(String command) {
        this.command = command;
    }

    private JoinGameStatus(String command, String message) {
        this(command);
        this.message = message;
    }

    public static JoinGameStatus joinSuccess() {
        return new JoinGameStatus("join-success");
    }

    public static JoinGameStatus joinFailed(String message) {
        return new JoinGameStatus("join-failed", message);
    }
}
