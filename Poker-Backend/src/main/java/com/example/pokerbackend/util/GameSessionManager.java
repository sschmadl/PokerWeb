package com.example.pokerbackend.util;

import com.example.pokerbackend.util.commands.ChatMessageCommand;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameSessionManager {
    private ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Pair<Player, WebSocketSession>> players = new ConcurrentHashMap<>();
    private ConcurrentHashMap<WebSocketSession, String> sessionToIdMap = new ConcurrentHashMap<>();

    private static GameSessionManager instance;
    private GameSessionManager() {
    }

    public static GameSessionManager getInstance() {
        if (instance == null) {
            instance = new GameSessionManager();
        }
        return instance;
    }

    public void addSession(GameSession session) {
        System.out.println("Adding session " + session.getGameId());
        sessions.put(session.getGameId(), session);
        System.out.println("Session count: " + sessions.size());
    }

    public void removeSession(String gameId) {
        System.out.println("Removing session " + gameId);
        sessions.remove(gameId);
        System.out.println("Remaining Sessions: " + sessions.size());
    }

    public List<Map<String, Object>> getGameSessionInfos() {
        List<Map<String, Object>> sessionInfo = new ArrayList<>();
        for (GameSession session : sessions.values()) {
            sessionInfo.add(session.getGameInfo());
        }
        return sessionInfo;
    }

    public void addPlayer(Player player, WebSocketSession session) {
        System.out.println("Adding player " + player.getName());
        players.put(player.getName(), new Pair<>(player, session));
        System.out.println("Player count: " + players.size());
    }

    public void removePlayer(Player player) {
        System.out.println("Removing player " + player.getName());
        players.remove(player.getName());
        System.out.println("Player count: " + players.size());
    }

    public void createSession(String name, int smallBlind, int bigBind, int maxPlayer, WebSocketSession creatingPlayer){
        GameSession gameSession = new GameSession(name, smallBlind, bigBind, maxPlayer);
        String username = creatingPlayer.getAttributes().get("username").toString();
        sessionToIdMap.put(creatingPlayer, gameSession.getGameId());
        Player player = players.get(username).a;
        gameSession.joinGame(player, creatingPlayer);
        addSession(gameSession);
    }

    public void joinGame(WebSocketSession webSocketSession, String username, String gameId){
        Player player = players.get(username).a;
        GameSession session = sessions.get(gameId);
        session.joinGame(player, webSocketSession);
    }

    public void sendMessage(WebSocketSession webSocketSession, ChatMessageCommand chatMessageCommand){
        String gameId = getIdFromWebsocketSession(webSocketSession);
        GameSession gameSession = sessions.get(gameId);
        gameSession.sendChatMessage(chatMessageCommand);
    }

    public void leave(String username, WebSocketSession webSocketSession){
        Player player = players.get(username).a;
        try {
            if (sessionToIdMap.containsKey(webSocketSession)) {
                String gameId = getIdFromWebsocketSession(webSocketSession);
                GameSession gameSession = sessions.get(gameId);
                gameSession.leave(player);
            }else {

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        removePlayer(player);
    }

    public String getIdFromWebsocketSession(WebSocketSession webSocketSession){
        return sessionToIdMap.get(webSocketSession);
    }

    public void addWebsocketToGameIdMapping(WebSocketSession webSocketSession, String gameId){
        sessionToIdMap.put(webSocketSession, gameId);
    }
}
