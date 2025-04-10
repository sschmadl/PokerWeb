package com.example.pokerbackend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSession {
    private static final int MAX_PLAYERS = 10;
    private int currentPlayer = 0;
    private GameInfo gameInfo = new GameInfo();
    private Map<String, Player> players = new HashMap<>();
    private PokerDeck deck = new PokerDeck();
    private List<PokerCard> communityCards = new ArrayList<>();

    public GameSession(String name, int smallBlind, int bigBlind, int currentPlayers, int maxPlayer) {
        gameInfo.setName(name);
        gameInfo.setPlayerCount(currentPlayers,maxPlayer);
        gameInfo.setSmallBlind(smallBlind);
        gameInfo.setBigBlind(bigBlind);
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }
}
