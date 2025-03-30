package com.example.pokerbackend.util;

import java.util.*;

public class PlayerHand {
    private final List<PokerCard> playerCards;
    private List<PokerCard> communityCards;

    // only build Hand if not already calculated
    private Integer bestHandForXCommunityCards = null;
    private List<PokerCard> bestHand = null;

    // varibales for checking if handranking was already calculated
    private Integer handRankingForXCommunityCards = null;
    private String handName;
    private Integer handRanking;

    public PlayerHand() {
        this.playerCards = new ArrayList<>();
    }

    public List<PokerCard> getPlayerCards(){
        return playerCards;
    }

    public void addCard(PokerCard card) {
        playerCards.add(card);
    }

    public void setCommunityCards(List<PokerCard> communityCards) {
        this.communityCards = communityCards;
    }

    public void reset() {
        playerCards.clear();
        handRankingForXCommunityCards = null;
        handName = null;
        bestHandForXCommunityCards = null;
        bestHand = null;
        handRanking = null;
    }

    public boolean isRoyalFlush() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> suitMap = makeSuitMap(allCards);

        List<PokerCard> listWithFlush = null;
        for (List<PokerCard> cardList : suitMap.values()) {
            if (cardList.size() >= 5 && testForFlush(cardList)) {
                listWithFlush = cardList;
                listWithFlush.sort(Comparator.comparingInt(PokerCard::getRankValue).reversed());
                break;
            }
        }
        // No Flush found
        if (listWithFlush == null) return false;

        String[] ranks = {"A", "K", "Q", "J", "10"};
        for (int i = 0; i < 5; i++) {
            if (!listWithFlush.get(i).getRank().equals(ranks[i])) return false;
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
        return testForFlush(allCards);
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

    public boolean isOnePair() {
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

    public int getHandRanking() {
        if (handRanking != null && handRankingForXCommunityCards == communityCards.size()) return handRanking;
        if (isRoyalFlush()) handRanking = 10;
        else if (isStraightFlush()) handRanking = 9;
        else if (isFourOfAKind()) handRanking = 8;
        else if (isFullHouse()) handRanking = 7;
        else if (isFlush()) handRanking = 6;
        else if (isStraight()) handRanking = 5;
        else if (isThreeOfAKind()) handRanking = 4;
        else if (isTwoPair()) handRanking = 3;
        else if (isOnePair()) handRanking = 2;
        else handRanking = 1;
        handRankingForXCommunityCards = communityCards.size();
        return handRanking;
    }

    public String getHandName() {
        if (handName != null && handRankingForXCommunityCards == communityCards.size()) return handName;
        handName = switch (getHandRanking()) {
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
        handRankingForXCommunityCards = communityCards.size();
        return handName;
    }

    public List<PokerCard> getBestHand() {
        if (bestHand != null && bestHandForXCommunityCards == communityCards.size()) return bestHand;
        bestHand = switch (getHandRanking()) {
            case 10 -> buildBestRoyalFlushHand();
            case 9 -> buildBestStraightFLush();
            case 8 -> buildBestFourOfAKind();
            case 7 -> buildBestFullHouse();
            case 6 -> buildBestFlush();
            case 5 -> buildBestStraight();
            case 4 -> buildBestThreeOfAKind();
            case 3 -> buildBestTwoPair();
            case 2 -> buildBestOnePair();
            case 1 -> buildBestHighCard();
            default -> null;
        };
        bestHandForXCommunityCards = communityCards.size();
        return bestHand;
    }

    // this Function assumes that the communityCards + playerCards contains a Royal flush
    public List<PokerCard> buildBestRoyalFlushHand() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};

        allCards.sort(Comparator.comparingInt(PokerCard::getRankValue).reversed());
        bestHand = allCards.subList(0, 6);
        bestHandForXCommunityCards = communityCards.size();
        return allCards.subList(0, 6);
    }

    // this function assumes that a straight flush is possible
    public List<PokerCard> buildBestStraightFLush() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> suitMap = makeSuitMap(allCards);
        for (List<PokerCard> cardList : suitMap.values()) {
            if (cardList.size() >= 5) {
                return buildBestStraight(cardList);
            }
        }
        return null;
    }

    // assumes four of a kind is possible and fills the hand to 5 cards
    public List<PokerCard> buildBestFourOfAKind() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);
        List<PokerCard> tempCards = new ArrayList<>();
        for (List<PokerCard> cardList : rankMap.values()) {
            if (cardList.size() >= 4) {
                tempCards.addAll(cardList);
                allCards.removeAll(cardList);
            }
        }
        fillWithKickers(tempCards, allCards);
        return tempCards;
    }

    // assumes full house is possible
    public List<PokerCard> buildBestFullHouse() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);
        List<PokerCard> tempCards = new ArrayList<>();
        List<List<PokerCard>> threeOfAKindList = new ArrayList<>();
        List<List<PokerCard>> twoPairList = new ArrayList<>();
        for (List<PokerCard> cardList : rankMap.values()) {
            if (cardList.size() >= 3) {
                threeOfAKindList.add(cardList);
            } else if (cardList.size() == 2) {
                twoPairList.add(cardList);
            }
        }
        if (threeOfAKindList.size() == 2) {
            if (threeOfAKindList.get(0).get(0).getRankValue() > threeOfAKindList.get(1).get(0).getRankValue()) {
                tempCards.addAll(threeOfAKindList.get(0));
                tempCards.addAll(threeOfAKindList.get(1).subList(0, 2));
            } else {
                tempCards.addAll(threeOfAKindList.get(1));
                tempCards.addAll(threeOfAKindList.get(0).subList(0, 2));
            }
        } else {
            tempCards.addAll(threeOfAKindList.get(0));
            List<PokerCard> highestTwoPair = null;
            for (List<PokerCard> twoPair : twoPairList) {
                if (highestTwoPair == null) {
                    highestTwoPair = twoPair;
                } else if (twoPair.get(0).getRankValue() > highestTwoPair.get(0).getRankValue()) {
                    highestTwoPair = twoPair;
                }
            }
            tempCards.addAll(highestTwoPair);
        }
        return tempCards;
    }

    //assumes Flush is possible
    public List<PokerCard> buildBestFlush() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> suitMap = makeSuitMap(allCards);
        for (List<PokerCard> cardList : suitMap.values()) {
            if (cardList.size() >= 5) {
                cardList.sort(Comparator.comparingInt(PokerCard::getRankValue).reversed());
                return cardList.subList(0,5);
            }
        }
        return null;
    }

    // assumes Straight is Possible
    public List<PokerCard> buildBestStraight(List<PokerCard> allCards) {
        List<PokerCard> tempCards = new ArrayList<>();
        allCards.sort(Comparator.comparingInt(PokerCard::getRankValue).reversed());
        for (PokerCard allCard : allCards) {
            if (tempCards.size() == 5) return tempCards;
            if (tempCards.isEmpty()) {
                tempCards.add(allCard);
            } else if (allCard.getRankValue() == tempCards.get(tempCards.size() - 1).getRankValue() - 1) {
                tempCards.add(allCard);
            } else if (allCard.getRankValue() == tempCards.get(tempCards.size() - 1).getRankValue()) {
                continue;
            } else {
                tempCards.clear();
                tempCards.add(allCard);
            }
        }
        if (tempCards.size() == 4 && tempCards.get(tempCards.size() - 1).getRankValue() == 2) {
            tempCards.add(allCards.get(0));
        }
        return tempCards;
    }
    public List<PokerCard> buildBestStraight() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        return buildBestStraight(allCards);
    }

    // assumes Three of a Kind is possible
    public List<PokerCard> buildBestThreeOfAKind() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);
        List<PokerCard> highestThreeOfAKind = null;
        for (List<PokerCard> cardList : rankMap.values()) {
            if (cardList.size() >= 3) {
                if (highestThreeOfAKind == null) {
                    highestThreeOfAKind = cardList;
                } else if (cardList.get(0).getRankValue() > highestThreeOfAKind.get(0).getRankValue()) {
                    highestThreeOfAKind = cardList;
                }
            }
        }
        List<PokerCard> tempCards = new ArrayList<>(highestThreeOfAKind);
        allCards.removeAll(highestThreeOfAKind);
        fillWithKickers(tempCards, allCards);
        return tempCards;
    }

    // assumes two pair is possible
    public List<PokerCard> buildBestTwoPair() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);
        List<List<PokerCard>> twoPairList = new ArrayList<>();
        for (List<PokerCard> cardList : rankMap.values()) {
            if (cardList.size() >= 2) {
                twoPairList.add(cardList);
            }
        }
        twoPairList.sort(Comparator.comparingInt(o -> o.get(0).getRankValue()));
        Collections.reverse(twoPairList);
        List<PokerCard> tempCards = new ArrayList<>();

        tempCards.addAll(twoPairList.get(0));
        allCards.removeAll(twoPairList.get(0));
        tempCards.addAll(twoPairList.get(1));
        allCards.removeAll(twoPairList.get(1));

        fillWithKickers(tempCards, allCards);
        return tempCards;
    }

    // assumes one pair is possible
    public List<PokerCard> buildBestOnePair() {
        List<PokerCard> allCards = new ArrayList<>() {{
            addAll(communityCards);
            addAll(playerCards);
        }};
        Map<String, List<PokerCard>> rankMap = makeRankMap(allCards);
        List<List<PokerCard>> onePairs = new ArrayList<>();
        for (List<PokerCard> cardList : rankMap.values()) {
            if (cardList.size() == 2) {
                onePairs.add(cardList);
            }
        }
        onePairs.sort(Comparator.comparingInt(o -> o.get(0).getRankValue()));
        Collections.reverse(onePairs);
        List<PokerCard> tempCards = new ArrayList<>(onePairs.get(0));
        allCards.removeAll(onePairs.get(0));
        fillWithKickers(tempCards, allCards);
        return tempCards;
    }

    public List<PokerCard> buildBestHighCard(){
        List<PokerCard> allCards = new ArrayList<>(){{
            addAll(communityCards);
            addAll(playerCards);
        }};
        allCards.sort(Comparator.comparingInt(PokerCard::getRankValue).reversed());
        List<PokerCard> tempCards = new ArrayList<>();
        for (PokerCard card : allCards) {
            if (tempCards.size() == 5) break;
            tempCards.add(card);
        }
        return tempCards;
    }

    private boolean testForFlush(List<PokerCard> allCards) {
        Map<String, List<PokerCard>> suitMap = makeSuitMap(allCards);
        for (List<PokerCard> cardList : suitMap.values()) {
            if (cardList.size() >= 5) {
                return true;
            }
        }
        return false;
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

    private void fillWithKickers(List<PokerCard> scoringHand, List<PokerCard> leftOverCards) {
        leftOverCards.sort(Comparator.comparingInt(PokerCard::getRankValue).reversed());
        for (PokerCard card : leftOverCards) {
            if (scoringHand.size() < 5) {
                scoringHand.add(card);
            } else {
                break;
            }
        }
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