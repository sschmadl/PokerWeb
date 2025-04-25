package com.example.pokerbackend.handler;

import com.example.pokerbackend.service.PlayerFactoryService;
import com.example.pokerbackend.util.GameSessionManager;
import com.example.pokerbackend.util.JwtUtil;
import com.example.pokerbackend.util.Player;
import com.example.pokerbackend.util.QueryUtils;
import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
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
        if ( !queryParams.containsKey("token")){
            System.out.println("Session closed: No token passed from " + session.getRemoteAddress().getAddress().getHostAddress());
            session.close();
            return;
        }
        String token = queryParams.get("token");
        System.out.println("Session opened: " + session.getRemoteAddress().getAddress().getHostAddress());

        if ( !jwtUtil.validateToken(token)){
            System.out.println("Session closed: Invalid token passed from " + session.getRemoteAddress().getAddress().getHostAddress());
            session.close();
            return;
        }

        String username = jwtUtil.extractUsername(token);
        System.out.println("Session opened: " + username);

        session.getAttributes().put("username", username);
        Player player = playerFactory.createPlayer(username);
        gameSessionManager.addPlayer(player);

        session.sendMessage(new TextMessage("Welcome " + username));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messageContent = message.getPayload();
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(messageContent, Map.class);
        System.out.println(messageContent);
        switch (map.get("command").toString()){
            case "create-game":
                System.out.println("Creating game");
                createGameCommand command = gson.fromJson(messageContent, createGameCommand.class);
                createLobby(session, command);
                break;
        }
    }

    private void createLobby(WebSocketSession session, createGameCommand command){

        String lobbyName = command.getName();
        int smallBlind = command.getSmallBind();
        int bigBlind = command.getBigBlind();
        int maxPlayerCount = command.getMaxPlayerCount();
        gameSessionManager.createSession(lobbyName,smallBlind,bigBlind,maxPlayerCount, session);
        try {
            session.sendMessage(new TextMessage("success"));
        }catch (Exception e){}
    }
}

class createGameCommand{
    String command;
    String name;
    int smallBind;
    int bigBlind;
    int maxPlayerCount;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getSmallBind() {
        return smallBind;
    }

    public void setSmallBind(int smallBind) {
        this.smallBind = smallBind;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}