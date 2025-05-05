package com.example.pokerbackend.util.commands;

import com.example.pokerbackend.util.Player;

public class PlayerJoinedGame {
    String command = "player-joined-game";
    String name;
    int credits;
    public PlayerJoinedGame(Player player) {
        this.name = player.getName();
        this.credits = player.getCredits();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
