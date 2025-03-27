package com.example.pokerbackend.util;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerHand {
    private List<PokerCard> playerCards;

    public PlayerHand() {
        this.playerCards = new ArrayList<>();
    }

    public void addCard(PokerCard card) {
        playerCards.add(card);
    }

    public void reset(){
        playerCards.clear();
    }

    public boolean isRoyalFlush(List<PokerCard> communityCards) {
        List<PokerCard> allCards = new ArrayList<>(){{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> suitMap = makeSuitMap(allCards);

        // Check if one appears atleast 5 times
        // If a list has atleast 5 then save in variable
        boolean listWith5CardsFounds = false;
        List<PokerCard> listWithAtleast5Cards = null;
        for (List<PokerCard> cardList : suitMap.values()){
            if (cardList.size() >= 5) {
                listWith5CardsFounds = true;
                listWithAtleast5Cards = cardList;
                listWithAtleast5Cards.sort(Comparator.comparingInt(PokerCard::getRankValue).reversed());
                break;
            }
        }
        if (!listWith5CardsFounds) return false;

        String[] ranks = {"A","K","Q","J","10"};
        for (int i = 0; i < 5; i++) {
            if (!listWithAtleast5Cards.get(i).getRank().equals(ranks[i])) return false;
        }
        return true;
    }

    public boolean isStraightFlush(List<PokerCard> communityCards) {
        return true; // @TODO das hier implementieren
    }

    public boolean isFourOfAKind(List<PokerCard> communityCards) {
        List<PokerCard> allCards = new ArrayList<>(){{
            addAll(communityCards);
            addAll(playerCards);
        }};
        // Group by rank
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);

        // Check if 4 Cards of a Rank are present
        for (List<PokerCard> cardList : rankMap.values()){
            if (cardList.size() == 4) {
                return true;
            }
        }

        return false;
    }

    public boolean isFullHouse(List<PokerCard> communityCards) {
        List<PokerCard> allCards = new ArrayList<>(){{
            addAll(communityCards);
            addAll(playerCards);
        }};
        // Group by rank
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);
        int listWith3CardsFounds = 0;
        int listWith2CardsFounds = 0;
        for (List<PokerCard> cardList : rankMap.values()){
            if (cardList.size() == 3) {
                listWith3CardsFounds++;
            }else if (cardList.size() == 2) {
                listWith2CardsFounds++;
            }
        }

        if (listWith3CardsFounds > 1) return true;
        return listWith3CardsFounds == 1 && listWith2CardsFounds >= 1;
    }

    private Map<String, List<PokerCard>> makeRankMap(List<PokerCard> allCards) {
        Map<String, List<PokerCard>> rankMap = new HashMap<>();
        for (PokerCard card : allCards) {
            if (rankMap.containsKey(card.getRank())){
                rankMap.get(card.getRank()).add(card);
            }else {
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
            if (rankMap.containsKey(card.getSuit())){
                rankMap.get(card.getSuit()).add(card);
            }else {
                List<PokerCard> cardList = new ArrayList<>();
                cardList.add(card);
                rankMap.put(card.getSuit(), cardList);
            }
        }
        return rankMap;
    }
}