package com.example.pokerbackend.util;

import org.antlr.v4.runtime.misc.Pair;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameSession {
    private String gameId = PokerGameIDGenerator.generateID();
    private String name;
    private int bigBlind;
    private int smallBlind;

    private final int MAX_PLAYERS;
    private ConcurrentHashMap<String, Pair<Player, WebSocketSession>> players = new ConcurrentHashMap<>();
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

    public void addPlayer(Player player, WebSocketSession session){
        players.put(player.getName(), new Pair<>(player, session));
    }

    public void broadCast(String message){
        for (Pair<Player, WebSocketSession> pair : players.values()) {
            try {
                pair.b.sendMessage(new TextMessage(message));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
