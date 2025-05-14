package com.example.pokerbackend.util.commands;

import com.example.pokerbackend.util.GameSession;

public class PlayerActionCommand {
    String command = "player-action";
    GameSession.Action action;
    int amount;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameSession.Action getAction() {
        return action;
    }
}
