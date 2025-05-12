package com.example.pokerbackend.util;

public class PokerCard {
    private String rank;
    private String suit;

    public PokerCard(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return String.format("%s of %s", rank, suit);
    }

    public int getRankValue() {
        switch (rank) {
            case "A": return 14;
            case "K": return 13;
            case "Q": return 12;
            case "J": return 11;
            default: return Integer.parseInt(rank);
        }
    }

    public String getFormattedCard(){
        if (getRankValue() < 10){
            return 9+suit.charAt(0)+"";
        }else if (getRankValue() == 10){
            return "T"+suit.charAt(0);
        }else if (getRankValue() > 10){
            return getRank()+suit.charAt(0);
        }
        return null;
    }
}
