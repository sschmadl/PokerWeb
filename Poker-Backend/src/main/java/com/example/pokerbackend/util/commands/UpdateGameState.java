package com.example.pokerbackend.util.commands;

public class UpdateGameState {
    String command = "update-game-state";
    boolean gameRunning;

    public UpdateGameState(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }
}
