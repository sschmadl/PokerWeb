package com.example.pokerbackend.util.commands;

public class PlayerLeaveCommand {
    String command = "player-left";
    String name;
    public PlayerLeaveCommand(String playerName) {
        this.name = playerName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPlayerName() {
        return name;
    }

    public void setPlayerName(String playerName) {
        this.name = playerName;
    }
}
