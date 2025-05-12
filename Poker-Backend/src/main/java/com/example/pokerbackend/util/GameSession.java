package com.example.pokerbackend.util;

import com.example.pokerbackend.util.commands.*;
import com.google.gson.Gson;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class GameSession {
    private String gameId = PokerGameIDGenerator.generateID();
    private String name;
    private int bigBlind;
    private int smallBlind;
    private static final Map<String, ReentrantLock> lobbyLocks = new ConcurrentHashMap<>();
    private static final GameSessionManager gameSessionManager = GameSessionManager.getInstance();
    private Gson gson = new Gson();
    private String admin;
    private boolean isJoinable = true;
    private int smallBlindIndex = 0;


    private final int MAX_PLAYERS;
    private ConcurrentHashMap<String, Pair<Player, WebSocketSession>> players = new ConcurrentHashMap<>();
    private List<Player> playerOrder = new ArrayList<>();
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
        gameInfo.put("gameId", this.gameId);
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
        if (playerOrder.isEmpty()) admin = player.getName();
        players.put(player.getName(), new Pair<>(player, session));
        playerOrder.add(player);
    }

    public void removePlayer(Player player){
        players.remove(player.getName());
        playerOrder.remove(player);
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

    public void broadCastExceptSender(String sendingPlayer, String message){
        System.out.println("broadcast: " + message);
        for (Pair<Player, WebSocketSession> pair : players.values()) {
            if (sendingPlayer.equals(pair.a.getName())) continue;
            try {
                pair.b.sendMessage(new TextMessage(message));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void joinGame(Player player, WebSocketSession session){
        ReentrantLock lock = lobbyLocks.computeIfAbsent(gameId, id -> new ReentrantLock());
        lock.lock();
        Gson gson = new Gson();
        try {
            if(!isJoinable){
                session.sendMessage(new TextMessage(gson.toJson(new ServerMessageCommand("Failed to joing","Table is already playing","red"))));
            } else if (players.size() == MAX_PLAYERS) {
                session.sendMessage(new TextMessage(gson.toJson(JoinGameStatus.joinFailed("Lobby is full"))));
            }else {
                addPlayer(player, session);

                // Inform player if joining is possible
                session.sendMessage(new TextMessage(gson.toJson(JoinGameStatus.joinSuccess())));
                gameSessionManager.addWebsocketToGameIdMapping(session, gameId);

                // Inform other Players
                PlayerJoinedGame playerJoinedGame = new PlayerJoinedGame(player);
                broadCastExceptSender(player.getName(), gson.toJson(playerJoinedGame));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void sendChatMessage(ChatMessageCommand chatMessageCommand){
        ReentrantLock lock = lobbyLocks.computeIfAbsent(gameId, id -> new ReentrantLock());
        lock.lock();
        try {
            broadCast(gson.toJson(chatMessageCommand));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void leave(Player player){
        ReentrantLock lock = lobbyLocks.computeIfAbsent(gameId, id -> new ReentrantLock());
        lock.lock();
        try{
            removePlayer(player);
            PlayerLeaveCommand  playerLeaveCommand = new PlayerLeaveCommand(player.getName());
            broadCastExceptSender(player.getName(), gson.toJson(playerLeaveCommand));
            if (players.isEmpty()) {
                gameSessionManager.removeSession(this.gameId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void startGame(){
        ReentrantLock lock = lobbyLocks.computeIfAbsent(gameId, id -> new ReentrantLock());
        lock.lock();
        try {
            isJoinable = false;
            UpdateGameState updateGameState = new UpdateGameState(true);
            broadCast(gson.toJson(updateGameState));
            for (Player player : playerOrder){
                player.getHand().reset();
            }
            communityCards.addAll(deck.dealCommunityCards());
            dealCardsToPlayers();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public List<Player> getPlayerOrder() {
        return playerOrder;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public boolean isJoinable() {
        return isJoinable;
    }

    public void setJoinable(boolean joinable) {
        isJoinable = joinable;
    }

    private void dealCardsToPlayers(){
        for(Player player : playerOrder){
            player.getHand().setCommunityCards(communityCards);
            player.getHand().addCard(deck.dealCard());
            player.getHand().addCard(deck.dealCard());

            WebSocketSession webSocketSession = players.get(player.getName()).b;
            try {
                webSocketSession.sendMessage(new TextMessage(gson.toJson(new PlayerCardsCommand(player.getHand().getPlayerCards()))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
