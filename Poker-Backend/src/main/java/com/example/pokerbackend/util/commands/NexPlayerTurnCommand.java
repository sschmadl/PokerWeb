package com.example.pokerbackend.util.commands;

public class NexPlayerTurnCommand {
    private String command = "player-next-turn";
    private String name;

    public NexPlayerTurnCommand(String name) {
        this.name = name;
    }
}
