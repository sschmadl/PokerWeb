package com.example.pokerbackend.util;

public class Player {
    private String name;
    private int credits;
    private PlayerHand hand;
    private int currentBet=0;
    private int totalContributionsToPot = 0;
    private boolean folded = false;

    public Player(String name, int credits) {
        this.name = name;
        this.credits = credits;
        this.hand = new PlayerHand();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public PlayerHand getHand() {
        return hand;
    }

    public void setHand(PlayerHand hand) {
        this.hand = hand;
    }

    public void subtractCredits(int credits) {
        this.credits -= credits;
        this.totalContributionsToPot += credits;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }

    public void resetTotalContributionsToPot(){
        this.totalContributionsToPot = 0;
    }

    public void addTotalContributionToPot(int amount){
        this.totalContributionsToPot += amount;
    }

    public void subtractTotalContributionToPot(int amount){
        this.totalContributionsToPot -= amount;
    }

    public int getTotalContributions(){
        return totalContributionsToPot;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }
}
