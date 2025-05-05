package com.example.pokerbackend.util;

import com.example.pokerbackend.util.commands.ChatMessageCommand;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameSessionManager {
    private ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Pair<Player, WebSocketSession>> players = new ConcurrentHashMap<>();

    private static GameSessionManager instance;
    private GameSessionManager() {
        // @Todo remove when no testcases are needed
        GameSession session = new GameSession("Not a real Game", 10, 20, 9);
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
        System.out.println("Adding session " + session);
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
        System.out.println("Adding player " + player);
        players.put(player.getName(), new Pair<>(player, session));
        System.out.println("Player count: " + players.size());
    }

    public void removePlayer(Player player) {
        System.out.println("Removing player " + player);
        players.remove(player.getName());
        System.out.println("Player count: " + players.size());
    }

    public void createSession(String name, int smallBlind, int bigBind, int maxPlayer, WebSocketSession creatingPlayer){
        GameSession gameSession = new GameSession(name, smallBlind, bigBind, maxPlayer);
        creatingPlayer.getAttributes().put("gameId", gameSession.getGameId());
        gameSession.addPlayer(players.get(creatingPlayer.getAttributes().get("username").toString()).a, creatingPlayer);
        addSession(gameSession);
    }

    public void joinGame(WebSocketSession webSocketSession, String username, String gameId){
        Player player = players.get(username).a;
        GameSession session = sessions.get(gameId);
        session.joinGame(player, webSocketSession);
    }

    public void sendMessage(WebSocketSession webSocketSession, ChatMessageCommand chatMessageCommand){
        String gameId = sessions.get("gameId").toString();
        GameSession gameSession = sessions.get(gameId);
        gameSession.sendChatMessage(chatMessageCommand);
    }

    public void leave(String username, WebSocketSession webSocketSession){
        String gameId = sessions.get("gameId").toString();
        GameSession gameSession = sessions.get(gameId);
        Player player = players.get(username).a;
        gameSession.leave(player);
        removePlayer(player);
    }
}
