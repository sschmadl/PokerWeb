package com.example.pokerbackend.util;

import com.example.pokerbackend.util.commands.*;
import com.google.gson.Gson;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

public class GameSession {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private String gameId = PokerGameIDGenerator.generateID();
    private String name;
    private int BIGBLIND;
    private int SMALLBLIND;
    private static final Map<String, ReentrantLock> lobbyLocks = new ConcurrentHashMap<>();
    private static final GameSessionManager gameSessionManager = GameSessionManager.getInstance();
    private Gson gson = new Gson();
    private String admin;

    private int smallBlindIndex = 0;
    private boolean bigBlindHasActed = false;
    private int highestBet = 0;
    private int currentPot = 0;
    private Player nextPlayer;
    private Player lastRaised;

    private GameState gameState = GameState.WAITING;

    public enum Action {
        BET, CHECK, FOLD, RAISE, CALL
    }

    private enum GameState {
        WAITING, PREFLOP, FLOP, TURN, RIVER, SHOWDOWN
    }


    private final int MAX_PLAYERS;
    private ConcurrentHashMap<String, Pair<Player, WebSocketSession>> players = new ConcurrentHashMap<>();
    private List<Player> playerOrder = new ArrayList<>();
    private List<Player> notFoldedPlayers;

    private PokerDeck deck = new PokerDeck();
    private List<PokerCard> communityCards = new ArrayList<>();

    public GameSession(String name, int smallBlind, int bigBlind, int maxPlayer) {
        this.name = name;
        this.SMALLBLIND = smallBlind;
        this.BIGBLIND = bigBlind;
        MAX_PLAYERS = maxPlayer;
    }

    public Map<String, Object> getGameInfo() {
        Map<String, Object> gameInfo = new HashMap<>();
        gameInfo.put("gameId", this.gameId);
        gameInfo.put("name", this.name);
        gameInfo.put("playerCount", players.size() + "/" + MAX_PLAYERS);
        gameInfo.put("bigBlind", this.BIGBLIND);
        gameInfo.put("smallBlind", this.SMALLBLIND);

        return gameInfo;
    }

    public String getGameId() {
        return gameId;
    }

    public void addPlayer(Player player, WebSocketSession session) {
        if (playerOrder.isEmpty()) admin = player.getName();
        players.put(player.getName(), new Pair<>(player, session));
        playerOrder.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player.getName());
        playerOrder.remove(player);
    }

    public void broadCast(String message) {
        for (Pair<Player, WebSocketSession> pair : players.values()) {
            try {
                pair.b.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadCastExceptSender(String sendingPlayer, String message) {
        System.out.println("broadcast: " + message);
        for (Pair<Player, WebSocketSession> pair : players.values()) {
            if (sendingPlayer.equals(pair.a.getName())) continue;
            try {
                pair.b.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void joinGame(Player player, WebSocketSession session) {
        ReentrantLock lock = lobbyLocks.computeIfAbsent(gameId, id -> new ReentrantLock());
        lock.lock();
        Gson gson = new Gson();
        try {
            if (gameState != GameState.WAITING) {
                session.sendMessage(new TextMessage(gson.toJson(new ServerMessageCommand("Failed to joing", "Table is already playing", "red"))));
            } else if (players.size() == MAX_PLAYERS) {
                session.sendMessage(new TextMessage(gson.toJson(JoinGameStatus.joinFailed("Lobby is full"))));
            } else {
                addPlayer(player, session);

                // Inform player if joining is possible
                session.sendMessage(new TextMessage(gson.toJson(JoinGameStatus.joinSuccess())));
                gameSessionManager.addWebsocketToGameIdMapping(session, gameId);

                // Inform other Players
                PlayerJoinedGame playerJoinedGame = new PlayerJoinedGame(player);
                broadCastExceptSender(player.getName(), gson.toJson(playerJoinedGame));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void sendChatMessage(ChatMessageCommand chatMessageCommand) {
        ReentrantLock lock = lobbyLocks.computeIfAbsent(gameId, id -> new ReentrantLock());
        lock.lock();
        try {
            broadCast(gson.toJson(chatMessageCommand));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void leave(Player player) {
        ReentrantLock lock = lobbyLocks.computeIfAbsent(gameId, id -> new ReentrantLock());
        lock.lock();
        try {
            removePlayer(player);
            PlayerLeaveCommand playerLeaveCommand = new PlayerLeaveCommand(player.getName());
            broadCastExceptSender(player.getName(), gson.toJson(playerLeaveCommand));
            if (players.isEmpty()) {
                gameSessionManager.removeSession(this.gameId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void startGame() {
        ReentrantLock lock = lobbyLocks.computeIfAbsent(gameId, id -> new ReentrantLock());
        lock.lock();
        try {
            notFoldedPlayers = new ArrayList<>(playerOrder);
            gameState = GameState.PREFLOP;
            UpdateGameState updateGameState = new UpdateGameState(true);
            bigBlindHasActed = false;
            int highestBet = 0;
            int currentPot = 0;
            int playerToMakeAMoveIndex = smallBlindIndex+1;
            int lastRaisedIndex = smallBlindIndex+2;
            broadCast(gson.toJson(updateGameState));
            postBlinds();
            for (Player player : playerOrder) {
                player.getHand().reset();
            }
            communityCards.addAll(deck.dealCommunityCards());
            dealCardsToPlayers();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
        return gameState == GameState.WAITING;
    }

    private void dealCardsToPlayers() {
        for (Player player : playerOrder) {
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

    public void handleAction(Player player, PlayerActionCommand playerActionCommand) {
        System.out.println("Playeraction: " + playerActionCommand.getAction());
        switch (playerActionCommand.getAction()) {
            case CHECK -> handleCheck(player, playerActionCommand);
            case BET -> handleBet(player, playerActionCommand);
            case CALL -> handleCall(player, playerActionCommand);
            case FOLD -> handleFold(player, playerActionCommand);
            case RAISE -> handleRaise(player, playerActionCommand);
        }
    }

    public void handleCheck(Player player, PlayerActionCommand playerActionCommand) {
        if (player == nextPlayer){
            broadCast(gson.toJson(new PlayerActionCommand(player.getName(),Action.CHECK)));
            announceNextPlayer();
        }
    }

    public void handleFold(Player player, PlayerActionCommand playerActionCommand) {

    }

    public void handleRaise(Player player, PlayerActionCommand playerActionCommand) {

    }

    public void handleBet(Player player, PlayerActionCommand playerActionCommand) {

    }

    public void handleCall(Player player, PlayerActionCommand playerActionCommand) {
        System.out.println("call received");
        System.out.println("player: " + player.getName());
        System.out.println("next Player: "+ nextPlayer.getName());
        if (player == nextPlayer){
            int difference = highestBet-player.getCurrentBet();
            currentPot+=difference;
            player.subtractCredits(difference);
            player.setCurrentBet(highestBet);
            broadCast(gson.toJson(new PlayerActionCommand(player.getName(), playerActionCommand.getAction(), player.getCurrentBet())));
            broadCast(gson.toJson(new NewCreditsCommand(player.getName(), player.getCredits())));
            announceNextPlayer();
        }
    }

    public void postBlinds() {
        Player smallBlind = notFoldedPlayers.get(smallBlindIndex % notFoldedPlayers.size());
        smallBlind.subtractCredits(SMALLBLIND);
        smallBlind.setCurrentBet(SMALLBLIND);
        Player bigBlind = notFoldedPlayers.get((smallBlindIndex + 1) % notFoldedPlayers.size());
        bigBlind.subtractCredits(BIGBLIND);
        bigBlind.setCurrentBet(BIGBLIND);

        broadCast(gson.toJson(new PlayerActionCommand(smallBlind.getName(), Action.BET, SMALLBLIND)));
        broadCast(gson.toJson(new PlayerActionCommand(bigBlind.getName(), Action.BET, BIGBLIND)));

        currentPot += SMALLBLIND + BIGBLIND;
        highestBet = BIGBLIND;
        lastRaised = notFoldedPlayers.get((smallBlindIndex+1)%notFoldedPlayers.size());
        nextPlayer = notFoldedPlayers.get((smallBlindIndex+1)%notFoldedPlayers.size());

        broadCast(gson.toJson(new NewCreditsCommand(smallBlind.getName(), smallBlind.getCredits())));
        broadCast(gson.toJson(new NewCreditsCommand(bigBlind.getName(), bigBlind.getCredits())));

        announceNextPlayer();
    }


    public void announceNextPlayer() {
        nextPlayer = notFoldedPlayers.get((notFoldedPlayers.indexOf(nextPlayer)+1)%notFoldedPlayers.size());
        if (nextPlayer == lastRaised){
            startNextBettingRound();
        }else {
            broadCast(gson.toJson(new NextPlayerTurnCommand(nextPlayer.getName())));
        }
    }

    public void startNextBettingRound() {
        highestBet = 0;
        nextPlayer = notFoldedPlayers.get(smallBlindIndex%notFoldedPlayers.size());
        lastRaised = nextPlayer;
        for (Player player : playerOrder){
            player.setCurrentBet(0);
        }
        switch (gameState) {
            case PREFLOP:
                gameState = GameState.FLOP;
                broadCast(gson.toJson(new NewBettingRoundCommand(currentPot)));
                broadCast(gson.toJson(new FlopCommand(communityCards.subList(0,3))));
                broadCast(gson.toJson(new ServerMessageCommand("New betting round", "Entering "+gameState, "blue")));

                broadCast(gson.toJson(new NextPlayerTurnCommand(nextPlayer.getName())));
                break;
            case FLOP:
                gameState = GameState.TURN;

                broadCast(gson.toJson(new NewBettingRoundCommand(currentPot)));


                broadCast(gson.toJson(new TurnCommand(communityCards.get(3))));
                broadCast(gson.toJson(new ServerMessageCommand("New betting round", "Entering "+gameState, "blue")));
                broadCast(gson.toJson(new NextPlayerTurnCommand(nextPlayer.getName())));
                break;
            case TURN:
                gameState = GameState.RIVER;


                broadCast(gson.toJson(new NewBettingRoundCommand(currentPot)));
                broadCast(gson.toJson(new RiverCommand(communityCards.get(4))));
                broadCast(gson.toJson(new ServerMessageCommand("New betting round", "Entering "+gameState, "blue")));
                broadCast(gson.toJson(new NextPlayerTurnCommand(nextPlayer.getName())));
                break;
            case RIVER:
                break;
        }
    }
}
