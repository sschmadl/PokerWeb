package com.example.pokerbackend.util.commands;

public class JoinGameCommand {
    String command;
    String gameId;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String lobbyId) {
        this.gameId = lobbyId;
    }
}
