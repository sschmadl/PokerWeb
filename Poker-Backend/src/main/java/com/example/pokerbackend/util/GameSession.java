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
import java.util.concurrent.TimeUnit;
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
    private int highestBet = 0;
    private int currentPot = 0;
    private Player nextPlayer;
    private Player lastRaised;
    private List<Player> allInsThisRound = new ArrayList<>();
    private List<SidePot> sidePots = new ArrayList<>();
    private List<Player> leftDuringRound = new ArrayList<>();

    private GameState gameState = GameState.WAITING;

    public enum Action {
        BET, CHECK, FOLD, RAISE, CALL, ALLIN
    }

    private enum GameState {
        WAITING, PREFLOP, FLOP, TURN, RIVER, SHOWDOWN
    }


    private final int MAX_PLAYERS;
    private ConcurrentHashMap<String, Pair<Player, WebSocketSession>> players = new ConcurrentHashMap<>();
    private List<Player> playerOrder = new ArrayList<>();
    private List<Player> allPlayers;

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
        System.out.println("Broadcast at" + System.currentTimeMillis());
        for (Pair<Player, WebSocketSession> pair : players.values()) {
            System.out.println("1");
            try {
                if (pair.b.isOpen()) {
                    pair.b.sendMessage(new TextMessage(message));
                }
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
                if (pair.b.isOpen()) {
                    pair.b.sendMessage(new TextMessage(message));
                }
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
            } else if(player.getCredits() == 0){
                session.sendMessage(new TextMessage(gson.toJson(new ServerMessageCommand("Failed to join","You have no credits (loser)","red"))));
            }
            else {
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
            if (gameState == GameState.WAITING) {
                removeFromGame(player);
            }else if (nextPlayer == player) {
                handleFold(player);
                removeFromGame(player);
            }else{
                leftDuringRound.add(player);
            }
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
            if (playerOrder.size() < 2){
                broadCast(gson.toJson(new ServerMessageCommand("Could not start game","There have to be atleast 2 players in game","red")));
            }else if (gameState == GameState.WAITING) {
                allPlayers = new ArrayList<>(playerOrder);
                gameState = GameState.PREFLOP;
                UpdateGameState updateGameState = new UpdateGameState(true);
                highestBet = 0;
                currentPot = 0;
                allInsThisRound.clear();
                sidePots.clear();
                broadCast(gson.toJson(updateGameState));
                for (Player player : playerOrder) {
                    player.getHand().reset();
                    player.setCurrentBet(0);
                    player.setFolded(false);
                    player.resetTotalContributionsToPot();
                }
                postBlinds();
                communityCards.addAll(deck.dealCommunityCards());
                dealCardsToPlayers();
                sendHandName();
            }

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
        ReentrantLock lock = lobbyLocks.computeIfAbsent(gameId, id -> new ReentrantLock());
        lock.lock();
        System.out.println("Playeraction: " + playerActionCommand.getAction());
        try {
            if (gameState != GameState.WAITING && gameState != GameState.SHOWDOWN) {
                switch (playerActionCommand.getAction()) {
                    case CHECK -> handleCheck(player, playerActionCommand);
                    case BET -> handleBet(player, playerActionCommand);
                    case CALL -> handleCall(player, playerActionCommand);
                    case FOLD -> handleFold(player);
                    case RAISE -> handleRaise(player, playerActionCommand);
                }
            }
            for (Player p : allPlayers) {
                System.out.println(p.getName() + " contributions: " + p.getTotalContributionsToPot());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void handleCheck(Player player, PlayerActionCommand playerActionCommand) {
        if (player == nextPlayer) {
            broadCast(gson.toJson(new PlayerActionCommand(player.getName(), Action.CHECK)));
            announceNextPlayer();
        }
    }

    public void handleFold(Player player) {
        if (player == nextPlayer) {
            broadCast(gson.toJson(new PlayerActionCommand(player.getName(), Action.FOLD)));
            player.setFolded(true);

            int notfoldedPlayers = (int) allPlayers.stream().filter(p -> !p.isFolded()).count();
            if (notfoldedPlayers == 1) {
                revealWinners();
            } else {
                announceNextPlayer();
            }
        }
    }

    public void handleRaise(Player player, PlayerActionCommand playerActionCommand) {
        if (player == nextPlayer) {
            int difference = highestBet - player.getCurrentBet()+playerActionCommand.getAmount();
            if (difference >= player.getCredits()) {
                player.addTotalContributionToPot(player.getCredits());
                player.setCurrentBet(player.getCurrentBet() + player.getCredits());
                currentPot += player.getCurrentBet();
                player.setCredits(0);
                if (player.getCurrentBet() > highestBet){
                    highestBet = player.getCurrentBet();
                    lastRaised = player;
                }
                highestBet = Math.max(highestBet, player.getCurrentBet());

                allInsThisRound.add(player);
                broadCast(gson.toJson(new NewCreditsCommand(player.getName(), player.getCredits())));
                broadCast(gson.toJson(new PlayerActionCommand(player.getName(), Action.ALLIN, highestBet)));
                announceNextPlayer();
            } else {
                System.out.println("Current highest Bet: " + highestBet);
                System.out.println("Raise amount: " + playerActionCommand.getAmount());
                System.out.println("Playerbet: " + player.getCurrentBet());
                highestBet += playerActionCommand.getAmount();
                lastRaised = player;

                player.addTotalContributionToPot(difference);
                player.subtractCredits(difference);
                player.setCurrentBet(highestBet);

                System.out.println("New highest Bet: " + highestBet);
                System.out.println("new Playerbet: " + player.getCurrentBet());

                broadCast(gson.toJson(new NewCreditsCommand(player.getName(), player.getCredits())));
                broadCast(gson.toJson(new PlayerActionCommand(player.getName(), Action.RAISE, player.getCurrentBet())));

                announceNextPlayer();
            }
        }
    }

    public void handleBet(Player player, PlayerActionCommand playerActionCommand) {
        if (player == nextPlayer) {
            if (playerActionCommand.getAmount() >= player.getCredits()) {
                player.addTotalContributionToPot(player.getCredits());
                player.setCurrentBet(player.getCurrentBet() + player.getCredits());
                currentPot += player.getCredits();
                player.setCredits(0);
                highestBet = player.getCurrentBet();
                lastRaised = player;

                allInsThisRound.add(player);
                broadCast(gson.toJson(new NewCreditsCommand(player.getName(), player.getCredits())));
                broadCast(gson.toJson(new PlayerActionCommand(player.getName(), Action.ALLIN, highestBet)));
                announceNextPlayer();
            } else {
                highestBet = playerActionCommand.getAmount();
                lastRaised = player;
                player.setCurrentBet(playerActionCommand.getAmount());
                player.subtractCredits(playerActionCommand.getAmount());
                player.addTotalContributionToPot(playerActionCommand.getAmount());
                currentPot += playerActionCommand.getAmount();
                broadCast(gson.toJson(new NewCreditsCommand(player.getName(), player.getCredits())));
                broadCast(gson.toJson(new PlayerActionCommand(player.getName(), Action.BET, playerActionCommand.getAmount())));
                announceNextPlayer();
            }
        }
    }

    public void handleCall(Player player, PlayerActionCommand playerActionCommand) {
        if (player == nextPlayer) {
            int difference = highestBet - player.getCurrentBet();
            System.out.println("Current Bet: " + player.getCurrentBet());
            System.out.println("Difference to pay: " + difference);
            if (difference >= player.getCredits()) {
                player.addTotalContributionToPot(player.getCredits());
                player.setCurrentBet(player.getCurrentBet() + player.getCredits());
                currentPot += player.getCurrentBet();
                player.setCredits(0);

                highestBet = Math.max(highestBet, player.getCurrentBet());

                allInsThisRound.add(player);
                broadCast(gson.toJson(new NewCreditsCommand(player.getName(), player.getCredits())));
                broadCast(gson.toJson(new PlayerActionCommand(player.getName(), Action.ALLIN, player.getCurrentBet())));
                announceNextPlayer();
            } else {
                currentPot += difference;
                player.addTotalContributionToPot(difference);
                player.subtractCredits(difference);
                player.setCurrentBet(highestBet);
                broadCast(gson.toJson(new PlayerActionCommand(player.getName(), playerActionCommand.getAction(), player.getCurrentBet())));
                broadCast(gson.toJson(new NewCreditsCommand(player.getName(), player.getCredits())));
                announceNextPlayer();
            }
        }
    }

    public void postBlinds() {
        System.out.println("postBlinds() called at " + System.currentTimeMillis());
        for (Player player : allPlayers) {
            System.out.println(player.getName() + " contributions: " + player.getTotalContributionsToPot());
        }

        Player smallBlind = allPlayers.get(smallBlindIndex % allPlayers.size());
        smallBlind.subtractCredits(SMALLBLIND);
        smallBlind.addTotalContributionToPot(SMALLBLIND);
        smallBlind.setCurrentBet(SMALLBLIND);

        Player bigBlind = allPlayers.get((smallBlindIndex + 1) % allPlayers.size());
        bigBlind.subtractCredits(BIGBLIND);
        bigBlind.addTotalContributionToPot(BIGBLIND);
        bigBlind.setCurrentBet(BIGBLIND);

        for (Player player : allPlayers) {
            System.out.println(player.getName() + " contributions: " + player.getTotalContributionsToPot());
        }

        broadCast(gson.toJson(new PlayerActionCommand(smallBlind.getName(), Action.BET, SMALLBLIND)));
        broadCast(gson.toJson(new PlayerActionCommand(bigBlind.getName(), Action.BET, BIGBLIND)));

        currentPot += SMALLBLIND + BIGBLIND;
        highestBet = BIGBLIND;
        lastRaised = allPlayers.get((smallBlindIndex + 1) % allPlayers.size());
        nextPlayer = allPlayers.get((smallBlindIndex + 1) % allPlayers.size());

        broadCast(gson.toJson(new NewCreditsCommand(smallBlind.getName(), smallBlind.getCredits())));
        broadCast(gson.toJson(new NewCreditsCommand(bigBlind.getName(), bigBlind.getCredits())));

        announceNextPlayer();
    }


    public void announceNextPlayer() {
        nextPlayer = allPlayers.get((allPlayers.indexOf(nextPlayer) + 1) % allPlayers.size());
        if (nextPlayer == lastRaised) {
            startNextBettingRound();
        } else {
            if (nextPlayer.getCredits() == 0 || nextPlayer.isFolded()) {
                announceNextPlayer();
            } else {
                broadCast(gson.toJson(new NextPlayerTurnCommand(nextPlayer.getName())));
            }
        }
        if (leftDuringRound.contains(nextPlayer)) {
            handleFold(nextPlayer);
            removeFromGame(nextPlayer);
            leftDuringRound.remove(nextPlayer);
            announceNextPlayer();
        }
    }

    public void startNextBettingRound() {
        highestBet = 0;
        nextPlayer = allPlayers.get(smallBlindIndex % allPlayers.size());
        lastRaised = nextPlayer;
        for (Player player : playerOrder) {
            player.setCurrentBet(0);
        }
        switch (gameState) {
            case PREFLOP:
                gameState = GameState.FLOP;
                broadCast(gson.toJson(new NewBettingRoundCommand(currentPot)));
                broadCast(gson.toJson(new FlopCommand(communityCards.subList(0, 3))));
                broadCast(gson.toJson(new ServerMessageCommand("New betting round", "Entering " + gameState, "blue")));

                if (nextPlayer.getCredits() == 0 || nextPlayer.isFolded()) {
                    announceNextPlayer();
                } else {
                    broadCast(gson.toJson(new NextPlayerTurnCommand(nextPlayer.getName())));
                }
                break;
            case FLOP:
                gameState = GameState.TURN;

                broadCast(gson.toJson(new NewBettingRoundCommand(currentPot)));
                broadCast(gson.toJson(new TurnCommand(communityCards.get(3))));
                broadCast(gson.toJson(new ServerMessageCommand("New betting round", "Entering " + gameState, "blue")));

                if (nextPlayer.getCredits() == 0 || nextPlayer.isFolded()) {
                    announceNextPlayer();
                } else {
                    broadCast(gson.toJson(new NextPlayerTurnCommand(nextPlayer.getName())));
                }
                break;
            case TURN:
                gameState = GameState.RIVER;


                broadCast(gson.toJson(new NewBettingRoundCommand(currentPot)));
                broadCast(gson.toJson(new RiverCommand(communityCards.get(4))));
                broadCast(gson.toJson(new ServerMessageCommand("New betting round", "Entering " + gameState, "blue")));

                if (nextPlayer.getCredits() == 0 || nextPlayer.isFolded()) {
                    announceNextPlayer();
                } else {
                    broadCast(gson.toJson(new NextPlayerTurnCommand(nextPlayer.getName())));
                }
                break;
            case RIVER:
                gameState = GameState.SHOWDOWN;
                revealWinners();
                break;
        }
        sendHandName();
    }

    public void revealWinners() {
        broadCast(gson.toJson(new ServerMessageCommand("Showdown", "Revealing Winners", "blue")));
        broadCast(gson.toJson(new RevealAllCards(new ArrayList<>(allPlayers))));
        System.out.println(gson.toJson(new RevealAllCards(new ArrayList<>(allPlayers))));

        // First build all side pots based on contributions
        for (Player player : allPlayers) {
            System.out.println(player.getName() + " contributions: " + player.getTotalContributionsToPot());
        }
        buildSidePots();
        for (SidePot sidePot : sidePots) {
            System.out.println(sidePot);
        }

        // Create a list of runnables for each pot reveal
        List<Runnable> revealTasks = new ArrayList<>();

        // Add initial delay before first pot reveal
        revealTasks.add(() -> {}); // Empty initial task for initial delay

        // Create tasks for each pot reveal
        for (int i = 0; i < sidePots.size(); i++) {
            final int potIndex = i;
            revealTasks.add(() -> {
                SidePot sidePot = sidePots.get(potIndex);
                revealPotWinners(sidePot, potIndex + 1, sidePots.size());
            });
        }

        // Add final tasks after all pots are revealed
        revealTasks.add(() -> {
            // Reset game for next round after a delay
            scheduler.schedule(() -> {
                resetGameForNextRound();
            }, 3, TimeUnit.SECONDS);
        });

        // Schedule all tasks with delays
        for (int i = 0; i < revealTasks.size(); i++) {
            scheduler.schedule(revealTasks.get(i), i * 4, TimeUnit.SECONDS);
        }
    }

    private void revealPotWinners(SidePot sidePot, int potNumber, int totalPots) {
        // Get all eligible players for this pot who haven't folded
        List<Player> eligiblePlayers = new ArrayList<>(sidePot.getEligiblePlayers());

        // Announce current pot being revealed
        broadCast(gson.toJson(new ServerMessageCommand(
                "Pot " + potNumber + "/" + totalPots,
                "Revealing winner for pot of " + sidePot.getAmount() + " chips",
                "blue")));

        // First reveal all eligible players' cards for this pot
        for (Player player : eligiblePlayers) {
            // Show this player's cards to everyone

            // Add a small delay between revealing each player's cards
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // If only one eligible player, they win the pot
        if (eligiblePlayers.size() == 1) {
            Player winner = eligiblePlayers.get(0);
            winner.setCredits(winner.getCredits() + sidePot.getAmount());
            broadCast(gson.toJson(new HiglightWinnerCommand(winner.getName())));
            broadCast(gson.toJson(new ServerMessageCommand(
                    "Pot Winner",
                    winner.getName() + " wins " + sidePot.getAmount() + " chips",
                    "green")));
            broadCast(gson.toJson(new NewCreditsCommand(winner.getName(), winner.getCredits())));
            return;
        }

        // Evaluate hands for all eligible players
        List<PlayerHandEvaluation> evaluations = new ArrayList<>();
        for (Player player : eligiblePlayers) {
            PlayerHand hand = player.getHand();
            evaluations.add(new PlayerHandEvaluation(
                    player,
                    hand.getHandRanking(),
                    hand.getBestHand()
            ));
        }

        // Sort evaluations by hand strength (descending)
        evaluations.sort((e1, e2) -> {
            // First compare hand rankings
            int rankingCompare = Integer.compare(e2.handRanking, e1.handRanking);
            if (rankingCompare != 0) return rankingCompare;

            // If same ranking, compare the actual best hands card by card
            for (int i = 0; i < e1.bestHand.size(); i++) {
                int cardCompare = Integer.compare(
                        e2.bestHand.get(i).getRankValue(),
                        e1.bestHand.get(i).getRankValue()
                );
                if (cardCompare != 0) return cardCompare;
            }
            return 0; // Exact same hand
        });

        // Find all winners (players with the same best hand)
        List<PlayerHandEvaluation> winners = new ArrayList<>();
        winners.add(evaluations.get(0));

        for (int i = 1; i < evaluations.size(); i++) {
            if (compareHandEvaluations(evaluations.get(0), evaluations.get(i)) == 0) {
                winners.add(evaluations.get(i));
            } else {
                break;
            }
        }

        // Split the pot among winners
        int chipsPerWinner = sidePot.getAmount() / winners.size();
        int remainder = sidePot.getAmount() % winners.size();

        for (int i = 0; i < winners.size(); i++) {
            Player winner = winners.get(i).player;
            int winnings = chipsPerWinner + (i < remainder ? 1 : 0);
            winner.setCredits(winner.getCredits() + winnings);

            // Show winning hand again with the announcement
            broadCast(gson.toJson(new HiglightWinnerCommand(winner.getName())));

            broadCast(gson.toJson(new ServerMessageCommand(
                    "Pot Winner",
                    winner.getName() + " wins " + winnings + " chips with " + winner.getHand().getHandName(),
                    "green")));
            broadCast(gson.toJson(new NewCreditsCommand(winner.getName(), winner.getCredits())));

            // Add delay between winner announcements
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void resetGameForNextRound() {
        smallBlindIndex = (smallBlindIndex + 1) % allPlayers.size();
        gameState = GameState.WAITING;
        communityCards.clear();
        deck.resetDeck();

        for (Player player : allPlayers) {
            if (player.getCredits() == 0) {
                WebSocketSession session = players.get(player.getName()).b;
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(gson.toJson(new RedirectCommand("/lobby-selection"))));
                        session.sendMessage(new TextMessage(gson.toJson(new ServerMessageCommand("Kicked from Game", "You got kicked for having 0 credits", "red"))));
                    } catch (Exception e) {}
                }
            }
        }

        // Check if game should continue (need at least 2 players)
        if (playerOrder.size() >= 2) {
            broadCast(gson.toJson(new ServerMessageCommand(
                    "Game Over",
                    "Waiting for admin to start next round",
                    "blue")));
        } else {
            broadCast(gson.toJson(new ServerMessageCommand(
                    "Game Over",
                    "Not enough players to continue",
                    "red")));
        }

        broadCast(gson.toJson(new UpdateGameState(false)));
    }

    public void buildSidePots() {
        List<Player> playersToEvaluate = new ArrayList<>(allPlayers);

        // Get unique contribution levels only from non-folded players
        Set<Integer> uniqueContributionLevels = new HashSet<>();
        for (Player player : playersToEvaluate) {
            if (!player.isFolded()) {
                int contrib = player.getTotalContributionsToPot();
                if (contrib > 0) {
                    uniqueContributionLevels.add(contrib);
                }
            }
        }

        List<Integer> sortedLevels = new ArrayList<>(uniqueContributionLevels);
        Collections.sort(sortedLevels);

        int previousLevel = 0;
        sidePots.clear();

        for (int level : sortedLevels) {
            int sidePotAmount = 0;
            Set<Player> eligiblePlayers = new HashSet<>();

            for (Player player : playersToEvaluate) {
                int contribution = player.getTotalContributionsToPot();
                int contributionForThisPot = Math.min(contribution, level - previousLevel);
                if (contributionForThisPot > 0) {
                    sidePotAmount += contributionForThisPot;
                    player.subtractTotalContributionToPot(contributionForThisPot);

                    // Eligible players are non-folded players who contributed here
                    if (!player.isFolded()) {
                        eligiblePlayers.add(player);
                    }
                }
            }

            if (sidePotAmount > 0 && !eligiblePlayers.isEmpty()) {
                SidePot sidePot = new SidePot(sidePotAmount);
                for (Player p : eligiblePlayers) {
                    sidePot.addEligiblePlayer(p);
                }
                sidePots.add(sidePot);
            } else {
                // If all contributors folded at this level, pot amount still adds to next pot,
                // so do NOT create a pot here.
                // The contributions remain subtracted, so no carry-over needed.
            }

            previousLevel = level;
        }

        // Add any remaining chips as final pot
        int finalPotAmount = 0;
        Set<Player> remainingEligible = new HashSet<>();
        for (Player player : playersToEvaluate) {
            int remaining = player.getTotalContributionsToPot();
            if (remaining > 0) {
                finalPotAmount += remaining;
                player.subtractTotalContributionToPot(remaining);
                if (!player.isFolded()) {
                    remainingEligible.add(player);
                }
            }
        }

        if (finalPotAmount > 0 && !remainingEligible.isEmpty()) {
            SidePot finalPot = new SidePot(finalPotAmount);
            for (Player p : remainingEligible) {
                finalPot.addEligiblePlayer(p);
            }
            sidePots.add(finalPot);
        }
    }

    // Helper class to evaluate player hands
    private static class PlayerHandEvaluation {
        Player player;
        int handRanking;
        List<PokerCard> bestHand;

        public PlayerHandEvaluation(Player player, int handRanking, List<PokerCard> bestHand) {
            this.player = player;
            this.handRanking = handRanking;
            this.bestHand = bestHand;
        }
    }

    // Helper method to compare two hand evaluations
    private int compareHandEvaluations(PlayerHandEvaluation e1, PlayerHandEvaluation e2) {
        // First compare hand rankings
        int rankingCompare = Integer.compare(e2.handRanking, e1.handRanking);
        if (rankingCompare != 0) return rankingCompare;

        // If same ranking, compare the actual best hands card by card
        for (int i = 0; i < e1.bestHand.size(); i++) {
            int cardCompare = Integer.compare(
                    e2.bestHand.get(i).getRankValue(),
                    e1.bestHand.get(i).getRankValue()
            );
            if (cardCompare != 0) return cardCompare;
        }
        return 0; // Exact same hand
    }

    private void removeFromGame(Player player){
        removePlayer(player);
        PlayerLeaveCommand playerLeaveCommand = new PlayerLeaveCommand(player.getName());
        broadCastExceptSender(player.getName(), gson.toJson(playerLeaveCommand));
        if (player.getName().equals(admin)){
            if (!playerOrder.isEmpty()) {
                admin = playerOrder.get(0).getName();
                broadCast(gson.toJson(new CurrentPlayersInfoCommand(playerOrder,admin)));
            }
        }
    }

    private void sendHandName(){
        for (Player player : allPlayers){
            WebSocketSession webSocketSession = players.get(player.getName()).b;
            try{
                webSocketSession.sendMessage(new TextMessage(gson.toJson(new BestHandName(player.getHand().getHandName()))));
            }catch (Exception e){}
        }
    }
}