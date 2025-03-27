package com.example.pokerbackend.service;

import com.example.pokerbackend.util.Player;
import com.example.pokerbackend.util.PokerGameIDGenerator;

import java.util.Map;

public class GameSession {
    private static  final int MAX_PLAYERS = 10;
    private int bigBlind;
    private int smallBlind;
    private String gameId;
    private Map<String, Player> players;


    public GameSession() {
        this.gameId = PokerGameIDGenerator.generateID();
    }
}
