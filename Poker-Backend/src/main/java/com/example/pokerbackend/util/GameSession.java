package com.example.pokerbackend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSession {
    private String gameId = PokerGameIDGenerator.generateID();
    private String name;
    private int bigBlind;
    private int smallBlind;

    private final int MAX_PLAYERS;
    private Map<String, Player> players = new HashMap<>();
    private PokerDeck deck = new PokerDeck();
    private List<PokerCard> communityCards = new ArrayList<>();

    public GameSession(String name, int smallBlind, int bigBlind, int maxPlayer) {
        this.name = name;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        MAX_PLAYERS = maxPlayer;
    }

    public Map<String, Object> getGameInfo() {
        Map<String, Object> gameInfo = new HashMap<>();
        gameInfo.put(gameId, this.gameId);
        gameInfo.put("name", this.name);
        gameInfo.put("playerCount", players.size()+"/"+MAX_PLAYERS);
        gameInfo.put("bigBlind",this.bigBlind);
        gameInfo.put("smallBlind",this.smallBlind);

        return gameInfo;
    }

    public String getGameId() {
        return gameId;
    }

    public void addPlayer(Player player){
        players.put(player.getName(), player);
    }
}
