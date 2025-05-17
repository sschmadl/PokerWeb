package com.example.pokerbackend.util.commands;

import com.example.pokerbackend.util.PokerCard;

public class TurnCommand {
    String command = "turn";
    String card;
    public TurnCommand(PokerCard card) {
        this.card = card.getFormattedCard();
    }
}
