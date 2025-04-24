package com.example.pokerbackend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSession {
    private String gameId = PokerGameIDGenerator.generateID();
    private String name;
    private int playerCount;
    private int bigBlind;
    private int smallBlind;

    private static final int MAX_PLAYERS = 10;
    private int currentPlayer = 0;
    private Map<String, Player> players = new HashMap<>();
    private PokerDeck deck = new PokerDeck();
    private List<PokerCard> communityCards = new ArrayList<>();

    public GameSession(String name, int smallBlind, int bigBlind, int currentPlayers, int maxPlayer) {
        this.name = name;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.playerCount = currentPlayers;
        this.currentPlayer = currentPlayers;
    }

    public Map<String, Object> getGameInfo() {
        Map<String, Object> gameInfo = new HashMap<>();
        gameInfo.put(gameId, this.gameId);
        gameInfo.put("name", this.name);
        gameInfo.put("playerCount", playerCount+"/"+MAX_PLAYERS);
        gameInfo.put("bigBlind",this.bigBlind);
        gameInfo.put("smallBlind",this.smallBlind);

        return gameInfo;
    }

    public String getGameId() {
        return gameId;
    }
}
