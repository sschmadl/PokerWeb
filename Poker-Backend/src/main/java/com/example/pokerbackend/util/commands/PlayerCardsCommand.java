package com.example.pokerbackend.util.commands;

import com.example.pokerbackend.util.PokerCard;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.List;

public class PlayerCardsCommand {
    String command = "player-cards";
    List<String> cards = new ArrayList<>();

    public PlayerCardsCommand(List<PokerCard> cards) {
        for (PokerCard card : cards) {
            this.cards.add(card.getFormattedCard());
        }
    }

    public List<String> getCards() {
        return cards;
    }

    public void setCards(List<String> cards) {
        this.cards = cards;
    }
}
