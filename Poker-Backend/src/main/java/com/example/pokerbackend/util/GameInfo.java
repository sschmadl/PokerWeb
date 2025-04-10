package com.example.pokerbackend.util;

public class GameInfo {
    private String gameId = PokerGameIDGenerator.generateID();
    private String name;
    private String playerCount;
    private int bigBlind;
    private int smallBlind;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount, int maxPlayers) {
        this.playerCount = playerCount + "/" + maxPlayers;
    }

    public int getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(int bigBlind) {
        this.bigBlind = bigBlind;
    }

    public int getSmallBlind() {
        return smallBlind;
    }

    public void setSmallBlind(int smallBlind) {
        this.smallBlind = smallBlind;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
