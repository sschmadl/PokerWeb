package com.example.pokerbackend.handler;

import com.example.pokerbackend.service.PlayerFactoryService;
import com.example.pokerbackend.util.*;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.pokerbackend.util.commands.*;

@Component
public class GameSessionWebsocketHandler extends TextWebSocketHandler {
    private final PlayerFactoryService playerFactory;
    private final JwtUtil jwtUtil;
    private GameSessionManager gameSessionManager = GameSessionManager.getInstance();
    private Gson gson = new Gson();

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
        String username;
        if (!jwtUtil.validateToken(token)) {
            System.out.println("received token: " + token);
            try {
                username = jwtUtil.extractUsername(token);
                System.out.println("expected token: " + jwtUtil.generateToken(username));
            } catch (Exception e) {
                System.out.println("Could not extract username");
            }
            System.out.println("Session closed: Invalid token passed from " + session.getRemoteAddress().getAddress().getHostAddress());
            session.close();
            return;
        }

        username = jwtUtil.extractUsername(token);
        System.out.println("Session opened: " + username);
        if (gameSessionManager.playerIsAlreadyConnected(username)) {
            ServerMessageCommand serverMessageCommand = new ServerMessageCommand(":(", "Someone is already connected with your account", "red");
            session.sendMessage(new TextMessage(gson.toJson(serverMessageCommand)));
            RedirectCommand redirectCommand = new RedirectCommand("/");
            session.sendMessage(new TextMessage(gson.toJson(redirectCommand)));
            session.close();
            return;
        }
        session.getAttributes().put("username", username);
        Player player = playerFactory.createPlayer(username);
        gameSessionManager.addPlayer(player, session);

        //session.sendMessage(new TextMessage("Welcome " + username));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messageContent = message.getPayload();
        Gson gson = new Gson();
        System.out.println("received Command: " + messageContent);
        try {
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
                    break;
                case "chat-message":
                    ChatMessageCommand chatMessageCommand = gson.fromJson(messageContent, ChatMessageCommand.class);
                    chatMessageCommand.setSender(session.getAttributes().get("username").toString());
                    gameSessionManager.sendMessage(session, chatMessageCommand);
                    break;
                case "current-players-info":
                    String gameId = gameSessionManager.getIdFromWebsocketSession(session);
                    GameSession gameSession = gameSessionManager.getGameSession(gameId);
                    List<Player> playerOrder = gameSession.getPlayerOrder();
                    CurrentPlayersInfoCommand currentPlayersInfoCommand = new CurrentPlayersInfoCommand(playerOrder, gameSession.getAdmin());
                    session.sendMessage(new TextMessage(gson.toJson(currentPlayersInfoCommand)));
                    break;
                case "leave-game":
                    String username = session.getAttributes().get("username").toString();
                    gameSessionManager.leave(username, session);
                    break;
                case "game-start":
                    gameSessionManager.startGame(session);
                    break;
                case "player-action":
                    PlayerActionCommand playerActionCommand = gson.fromJson(messageContent, PlayerActionCommand.class);
                    playerActionCommand.setName(session.getAttributes().get("username").toString());
                    gameSessionManager.handlePlayerAction(session, playerActionCommand);
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.sendMessage(new TextMessage(gson.toJson(new ServerMessageCommand("Error", "Illegal command", "red"))));
        }
    }

    private void createLobby(WebSocketSession session, CreateGameCommand command) {
        Player player = gameSessionManager.getPlayers().get(session.getAttributes().get("username").toString()).a;
        if (player.getCredits() == 0) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(gson.toJson(new ServerMessageCommand("Couldn't create a Lobby", "You can't create a lobby with 0 credits(loser)", "red"))));
                }catch (Exception e){}
            }
        } else {
            String lobbyName = command.getGameName();
            if (lobbyName.isEmpty()) lobbyName = session.getAttributes().get("username").toString() + "'s Game";
            int smallBlind = command.getSmallBlind();
            int bigBlind = command.getBigBlind();
            int maxPlayerCount = command.getMaxPlayerCount();
            GameSession gameSession = gameSessionManager.createSession(lobbyName, smallBlind, bigBlind, maxPlayerCount, session);
            try {
                session.sendMessage(new TextMessage("success"));
                CurrentPlayersInfoCommand currentPlayersInfoCommand = new CurrentPlayersInfoCommand(gameSession.getPlayerOrder(), gameSession.getAdmin());
                session.sendMessage(new TextMessage(gson.toJson(currentPlayersInfoCommand)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void joinGame(WebSocketSession session, JoinGameCommand command) {
        if (command.getGameId() != null) {
            System.out.println(session.getAttributes().get("username") + "is attempting to join " + command.getGameId());
            gameSessionManager.joinGame(session, session.getAttributes().get("username").toString(), command.getGameId());
        } else {
            try {
                session.sendMessage(new TextMessage(gson.toJson(new ServerMessageCommand("O_O", "Heast wähl a spü aus", "red"))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Player attempted to join a lobby but sent no Id");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (!session.getAttributes().containsKey("username")) return;
        String username = session.getAttributes().get("username").toString();
        gameSessionManager.leaveDisconnect(username, session);
    }
}



