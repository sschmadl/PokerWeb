package com.example.pokerbackend.util;

import java.util.*;

public class PlayerHand {
    private final List<PokerCard> playerCards;
    private List<PokerCard> communityCards;

    public PlayerHand() {
        this.playerCards = new ArrayList<>();
    }

    public void addCard(PokerCard card) {
        playerCards.add(card);
    }

    public void setCommunityCards(List<PokerCard> communityCards) {
        this.communityCards = communityCards;
    }

    public void reset() {
        playerCards.clear();
    }

    public boolean isRoyalFlush() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> suitMap = makeSuitMap(allCards);

        // Check if one appears atleast 5 times
        // If a list has atleast 5 then save in variable
        boolean listWith5CardsFounds = false;
        List<PokerCard> listWithAtleast5Cards = null;
        for (List<PokerCard> cardList : suitMap.values()) {
            if (cardList.size() >= 5) {
                listWith5CardsFounds = true;
                listWithAtleast5Cards = cardList;
                listWithAtleast5Cards.sort(Comparator.comparingInt(PokerCard::getRankValue).reversed());
                break;
            }
        }
        if (!listWith5CardsFounds) return false;

        String[] ranks = {"A", "K", "Q", "J", "10"};
        for (int i = 0; i < 5; i++) {
            if (!listWithAtleast5Cards.get(i).getRank().equals(ranks[i])) return false;
        }
        return true;
    }

    public boolean isStraightFlush() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> suitMap = makeSuitMap(allCards);
        for (List<PokerCard> cardList : suitMap.values()) {
            if (cardList.size() >= 5 && testForStraigth(cardList)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFourOfAKind() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        // Group by rank
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);

        // Check if 4 Cards of a Rank are present
        for (List<PokerCard> cardList : rankMap.values()) {
            if (cardList.size() == 4) {
                return true;
            }
        }

        return false;
    }

    public boolean isFullHouse() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        // Group by rank
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);
        int listWith3CardsFounds = 0;
        int listWith2CardsFounds = 0;
        for (List<PokerCard> cardList : rankMap.values()) {
            if (cardList.size() == 3) {
                listWith3CardsFounds++;
            } else if (cardList.size() == 2) {
                listWith2CardsFounds++;
            }
        }

        if (listWith3CardsFounds > 1) return true;
        return listWith3CardsFounds == 1 && listWith2CardsFounds >= 1;
    }

    public boolean isFlush() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> suitMap = makeSuitMap(allCards);
        for (List<PokerCard> cardList : suitMap.values()) {
            if (cardList.size() >= 5) {
                return true;
            }
        }
        return false;
    }

    public boolean isStraight() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        return testForStraigth(allCards);
    }

    public boolean isThreeOfAKind() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);
        for (List<PokerCard> cardList : rankMap.values()) {
            if (cardList.size() >= 3) {
                return true;
            }
        }
        return false;
    }

    public boolean isTwoPair() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);
        int listWith2CardsFounds = 0;
        for (List<PokerCard> cardList : rankMap.values()) {
            if (cardList.size() == 2) {
                listWith2CardsFounds++;
            }
        }
        return listWith2CardsFounds >= 2;
    }

    public boolean isOnePair(){
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);
        int listWith2CardsFounds = 0;
        for (List<PokerCard> cardList : rankMap.values()) {
            if (cardList.size() == 2) {
                listWith2CardsFounds++;
            }
        }
        return listWith2CardsFounds == 1;
    }

    public int getHandRanking(){
        if(isRoyalFlush())return 10;
        if(isStraightFlush())return 9;
        if(isFourOfAKind())return 8;
        if(isFullHouse())return 7;
        if(isFlush())return 6;
        if(isStraight())return 5;
        if(isThreeOfAKind())return 4;
        if(isTwoPair())return 3;
        if(isOnePair())return 2;
        return 1;
    }

    public String getHandName(){
        return switch (getHandRanking()) {
            case 10 -> "Royal Flush";
            case 9 -> "Straight Flush";
            case 8 -> "Four of a Kind";
            case 7 -> "Full House";
            case 6 -> "Flush";
            case 5 -> "Straight";
            case 4 -> "Three of a Kind";
            case 3 -> "Two Pair";
            case 2 -> "One Pair";
            default -> "High Card";
        };
    }

    private boolean testForStraigth(List<PokerCard> allCards) {
        if (allCards.size() < 5) return false;
        allCards.sort(Comparator.comparingInt(PokerCard::getRankValue).reversed());
        int ranksInOrder = 1;
        for (int i = 0; i < allCards.size(); i++) {
            if (ranksInOrder == 5) return true;
            if (allCards.get(i).getRankValue() == allCards.get((i + 1) % allCards.size()).getRankValue() + 1) {
                ranksInOrder++;

                // Special case if Ace is counted as a 1
            } else if (allCards.get(i).getRankValue() == 2 && allCards.get(0).getRankValue() == 14) {
                ranksInOrder++;

                // if list contains the same value more than once
            } else if (allCards.get(i).getRankValue() == allCards.get((i + 1) % allCards.size()).getRankValue()) {
                continue;
            } else {
                ranksInOrder = 1;
            }
        }
        return ranksInOrder >= 5;
    }

    private Map<String, List<PokerCard>> makeRankMap(List<PokerCard> allCards) {
        Map<String, List<PokerCard>> rankMap = new HashMap<>();
        for (PokerCard card : allCards) {
            if (rankMap.containsKey(card.getRank())) {
                rankMap.get(card.getRank()).add(card);
            } else {
                List<PokerCard> cardList = new ArrayList<>();
                cardList.add(card);
                rankMap.put(card.getRank(), cardList);
            }
        }
        return rankMap;
    }

    private Map<String, List<PokerCard>> makeSuitMap(List<PokerCard> allCards) {
        Map<String, List<PokerCard>> rankMap = new HashMap<>();
        for (PokerCard card : allCards) {
            if (rankMap.containsKey(card.getSuit())) {
                rankMap.get(card.getSuit()).add(card);
            } else {
                List<PokerCard> cardList = new ArrayList<>();
                cardList.add(card);
                rankMap.put(card.getSuit(), cardList);
            }
        }
        return rankMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PokerCard card : playerCards) {
            sb.append(card.toString()).append("\n");
        }
        return sb.toString();
    }
}