package com.example.pokerbackend.util;

import java.util.*;

public class PokerDeck {
    private static final String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
    private List<PokerCard> deck;
    private Random rand;

    public PokerDeck() {
        deck = new ArrayList<>();
        rand = new Random();
        resetDeck();
    }

    // Shuffle the deck
    public void shuffle() {
        Collections.shuffle(deck, rand);
    }

    // Deal a card from the deck
    public PokerCard dealCard() {
        if (!deck.isEmpty()) {
            return deck.remove(deck.size() - 1);
        }
        return null;  // No more cards left
    }

    // Get the number of cards left in the deck
    public int getCardsLeft() {
        return deck.size();
    }

    // Deal community cards
    public List<PokerCard> dealCommunityCards(int amount) {
        List<PokerCard> communityCards = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            communityCards.add(dealCard());
        }
        return communityCards;
    }

    public List<PokerCard> dealCommunityCards() {
        return dealCommunityCards(5);
    }

    public void resetDeck() {
        deck.clear();
        // Initialize the deck with all 52 cards
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new PokerCard(rank, suit));
            }
        }
        shuffle();
    }
}
