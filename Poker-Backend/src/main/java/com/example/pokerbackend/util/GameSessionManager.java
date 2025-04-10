package com.example.pokerbackend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSessionManager {
    private Map<String, GameSession> sessions = new HashMap<>();

    private static GameSessionManager instance;
    private GameSessionManager() {
        // @Todo remove when no testcases are needed
        GameSession session = new GameSession("Leon stinkt", 10, 20, 6, 9);
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
        sessions.put(session.getGameInfo().getGameId(), session);
    }

    public List<GameInfo> getGameSessionInfos() {
        List<GameInfo> sessionInfo = new ArrayList<>();
        for (GameSession session : sessions.values()) {
            sessionInfo.add(session.getGameInfo());
        }
        return sessionInfo;
    }
}
