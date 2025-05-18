package com.example.pokerbackend.util.commands;

import com.example.pokerbackend.util.Player;
import com.example.pokerbackend.util.PokerCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RevealAllCards {
    String command = "reveal-all-cards";
    ArrayList<Map<Object, Object>> players = new ArrayList<>();

    public RevealAllCards(ArrayList<Player> players) {
        ArrayList<String> cards = new ArrayList<>();
        for (Player player : players) {
            Map<Object, Object> map = new HashMap<>();
            cards.clear();
            for (PokerCard card : player.getHand().getPlayerCards()){
                cards.add(card.getFormattedCard());
            }
            map.put("cards", cards);
            map.put("name", player.getName());
            this.players.add(map);
        }
    }
}
