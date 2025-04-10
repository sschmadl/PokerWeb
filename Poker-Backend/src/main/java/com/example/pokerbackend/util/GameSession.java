package com.example.pokerbackend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSession {
    private GameInfo gameInfo = new GameInfo();
    private Map<String, Player> players = new HashMap<>();
    private PokerDeck deck = new PokerDeck();
    private List<PokerCard> communityCards = new ArrayList<>();

    public GameSession(String name, int playerCount, int smallBlind, int bigBlind) {
        gameInfo.setName(name);
        gameInfo.setPlayerCount(playerCount);
        gameInfo.setSmallBlind(smallBlind);
        gameInfo.setBigBlind(bigBlind);
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }
}
