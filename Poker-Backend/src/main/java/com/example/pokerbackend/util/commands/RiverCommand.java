package com.example.pokerbackend.util.commands;

import com.example.pokerbackend.util.PokerCard;

public class RiverCommand {
    String command = "river";
    String card;
    public RiverCommand(PokerCard card) {
        this.card = card.getFormattedCard();
    }
}
