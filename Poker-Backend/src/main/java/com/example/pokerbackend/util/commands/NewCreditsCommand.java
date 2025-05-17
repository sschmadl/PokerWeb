package com.example.pokerbackend.util.commands;

public class NewCreditsCommand {
    private String command = "new-credits";
    private String name;
    private int amount;

    public NewCreditsCommand(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }
}
