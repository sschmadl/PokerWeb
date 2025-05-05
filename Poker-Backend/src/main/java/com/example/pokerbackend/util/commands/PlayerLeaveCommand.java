package com.example.pokerbackend.util.commands;

public class PlayerLeaveCommand {
    String command = "player-left";
    String playerName;
    public PlayerLeaveCommand(String playerName) {
        this.playerName = playerName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
