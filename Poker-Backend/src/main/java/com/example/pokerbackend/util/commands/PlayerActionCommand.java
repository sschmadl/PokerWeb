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

    public PlayerActionCommand(String name, GameSession.Action action, int amount) {
        this(name,action);
        this.amount = amount;
    }

    public PlayerActionCommand(String name, GameSession.Action action) {
        this.action = action;
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
