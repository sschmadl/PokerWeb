package com.example.pokerbackend.util;

import com.example.pokerbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class Player {
    private String name;
    private int credits;
    private PlayerHand hand;
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
}
