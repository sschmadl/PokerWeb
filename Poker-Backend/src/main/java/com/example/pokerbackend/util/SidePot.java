package com.example.pokerbackend.util;

import com.example.pokerbackend.util.Player;

import java.util.HashSet;
import java.util.Set;

public class SidePot {
    private int amount;
    private Set<Player> eligiblePlayers;

    public SidePot(int amount) {
        this.amount = amount;
        this.eligiblePlayers = new HashSet<>();
    }

    public int getAmount() { return amount; }
    public void addAmount(int amount){this.amount += amount;}
    public Set<Player> getEligiblePlayers() { return eligiblePlayers; }
    public void addEligiblePlayer(Player player) { eligiblePlayers.add(player); }

    @Override
    public String toString() {
        return "SidePot{" +
                "amount=" + amount +
                ", eligiblePlayers=" + eligiblePlayers +
                '}';
    }
}