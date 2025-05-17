package com.example.pokerbackend.util.commands;

public class NextPlayerTurnCommand {
    private String command = "player-next-turn";
    private String name;

    public NextPlayerTurnCommand(String name) {
        this.name = name;
    }
}
