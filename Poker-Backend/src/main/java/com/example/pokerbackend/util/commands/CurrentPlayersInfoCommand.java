package com.example.pokerbackend.util.commands;

import java.util.ArrayList;
import java.util.List;

public class CurrentPlayersInfoCommand {
    String command = "current-players-info";
    List players = new ArrayList();

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List getPlayers() {
        return players;
    }

    public void setPlayers(List players) {
        this.players = players;
    }
}