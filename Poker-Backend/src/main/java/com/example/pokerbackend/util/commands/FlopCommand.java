package com.example.pokerbackend.util.commands;

import com.example.pokerbackend.util.PokerCard;

import java.util.ArrayList;
import java.util.List;

public class FlopCommand {
    String command = "flop";
    List<String> cards = new ArrayList<>();
    public FlopCommand(List<PokerCard> card) {
        for (PokerCard pokerCard : card) {
            this.cards.add(pokerCard.getFormattedCard());
        }
    }
}
