package com.example.pokerbackend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSessionManager {
    private Map<String, GameSession> sessions = new HashMap<>();
    private Map<String, Player> players = new HashMap<>();

    private static GameSessionManager instance;
    private GameSessionManager() {
        // @Todo remove when no testcases are needed
        GameSession session = new GameSession("Not a real Game", 10, 20, 6, 9);
        addSession(session);
        //
    }

    public static GameSessionManager getInstance() {
        if (instance == null) {
            instance = new GameSessionManager();
        }
        return instance;
    }

    public void addSession(GameSession session) {
        sessions.put(session.getGameId(), session);
    }

    public List<Map<String, Object>> getGameSessionInfos() {
        List<Map<String, Object>> sessionInfo = new ArrayList<>();
        for (GameSession session : sessions.values()) {
            sessionInfo.add(session.getGameInfo());
        }
        return sessionInfo;
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), player);
    }
}
