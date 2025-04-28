package com.example.pokerbackend.handler;

import com.example.pokerbackend.service.PlayerFactoryService;
import com.example.pokerbackend.util.GameSessionManager;
import com.example.pokerbackend.util.JwtUtil;
import com.example.pokerbackend.util.Player;
import com.example.pokerbackend.util.QueryUtils;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
public class GameSessionWebsocketHandler extends TextWebSocketHandler {
    private final PlayerFactoryService playerFactory;
    private final JwtUtil jwtUtil;
    private GameSessionManager gameSessionManager = GameSessionManager.getInstance();

    public GameSessionWebsocketHandler(PlayerFactoryService playerFactory, JwtUtil jwtUtil) {
        System.out.println("GameSessionWebsocketHandler created");
        this.playerFactory = playerFactory;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Incoming connection");
        URI uri = session.getUri();
        String query = uri.getQuery();

        Map<String, String> queryParams = QueryUtils.splitQuery(query);
        // Close Session if token isn't passed in url
        if (!queryParams.containsKey("token")) {
            System.out.println("Session closed: No token passed from " + session.getRemoteAddress().getAddress().getHostAddress());
            session.close();
            return;
        }
        String token = queryParams.get("token");
        System.out.println("Session opened: " + session.getRemoteAddress().getAddress().getHostAddress());

        if (!jwtUtil.validateToken(token)) {
            System.out.println("Session closed: Invalid token passed from " + session.getRemoteAddress().getAddress().getHostAddress());
            session.close();
            return;
        }

        String username = jwtUtil.extractUsername(token);
        System.out.println("Session opened: " + username);

        session.getAttributes().put("username", username);
        Player player = playerFactory.createPlayer(username);
        gameSessionManager.addPlayer(player, session);

        Map<String, String> map = new HashMap<>();
        map.put("command","server-message");
        map.put("message", "test Server message");
        Gson gson = new Gson();
        session.sendMessage(new TextMessage(gson.toJson(map)));
       //session.sendMessage(new TextMessage("Welcome " + username));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messageContent = message.getPayload();
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(messageContent, Map.class);
        System.out.println(messageContent);
        switch (map.get("command").toString()) {
            case "create-game":
                System.out.println("Creating game");
                CreateGameCommand createGameCommand = gson.fromJson(messageContent, CreateGameCommand.class);
                createLobby(session, createGameCommand);
                break;
            case "join-game":
                JoinGameCommand joinGameCommand = gson.fromJson(messageContent, JoinGameCommand.class);
                joinGame(session, joinGameCommand);
        }
    }

    private void createLobby(WebSocketSession session, CreateGameCommand command) {

        String lobbyName = command.getGameName();
        int smallBlind = command.getSmallBlind();
        int bigBlind = command.getBigBlind();
        int maxPlayerCount = command.getMaxPlayerCount();
        gameSessionManager.createSession(lobbyName, smallBlind, bigBlind, maxPlayerCount, session);
        try {
            session.sendMessage(new TextMessage("success"));
        } catch (Exception e) {
        }
    }

    private void joinGame(WebSocketSession session, JoinGameCommand command) {
        System.out.println(session.getAttributes().get("username") + "is attempting to join ");
    }
}

class CreateGameCommand {

    String command;
    String gameName;
    int smallBlind;
    int bigBlind;
    int maxPlayerCount;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getSmallBlind() {
        return smallBlind;
    }

    public void setSmallBlind(int smallBlind) {
        this.smallBlind = smallBlind;
    }

    public int getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(int bigBlind) {
        this.bigBlind = bigBlind;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }
}

class JoinGameCommand {
    String command;
    String lobbyId;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }
}