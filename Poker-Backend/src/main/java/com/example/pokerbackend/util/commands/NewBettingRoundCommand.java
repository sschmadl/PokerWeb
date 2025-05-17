package com.example.pokerbackend.util.commands;

public class NewBettingRoundCommand {
    private String command = "new-betting-round";
    private int pot;

    public NewBettingRoundCommand(int pot) {
        this.pot = pot;
    }
}
