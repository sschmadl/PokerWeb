package com.example.pokerbackend.util;

import com.example.pokerbackend.model.User;
import com.example.pokerbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

public class Player {
    private String name;
    private int credits;
    private PlayerHand hand;
    private int currentBet=0;
    private int totalContributionsToPot = 0;
    private boolean folded = false;

    private final UserRepository userRepository;
    private final User user;

    public Player(String name, int credits, UserRepository userRepository) {
        this.name = name;
        this.credits = credits;
        this.hand = new PlayerHand();
        this.userRepository = userRepository;
        user = userRepository.findUserByUsername(name);
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
        user.setCredits(credits);
        userRepository.save(user);
    }

    public PlayerHand getHand() {
        return hand;
    }

    public void setHand(PlayerHand hand) {
        this.hand = hand;
    }

    public void subtractCredits(int credits) {
        this.credits -= credits;
        user.setCredits(this.credits);
        userRepository.save(user);
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
        System.out.println(name+" added "+amount);
    }

    public void subtractTotalContributionToPot(int amount){
        this.totalContributionsToPot -= amount;
    }

    public int getTotalContributionsToPot(){
        return totalContributionsToPot;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", totalContributionsToPot=" + totalContributionsToPot +
                '}';
    }
}
