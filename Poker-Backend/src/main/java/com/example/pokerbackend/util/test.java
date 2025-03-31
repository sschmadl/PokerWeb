package com.example.pokerbackend.util;

import java.util.*;

public class test {
    public static void main(String[] args) {
        test4();
    }

    public static void test2() {
        Map<String, Integer> statisticMap = new LinkedHashMap<>();
        statisticMap.put("Royal Flush", 0);
        statisticMap.put("Straight Flush", 0);
        statisticMap.put("Four of a Kind", 0);
        statisticMap.put("Full House", 0);
        statisticMap.put("Flush", 0);
        statisticMap.put("Straight", 0);
        statisticMap.put("Three of a Kind", 0);
        statisticMap.put("Two Pair", 0);
        statisticMap.put("One Pair", 0);
        statisticMap.put("High Card", 0);
        PokerDeck deck = new PokerDeck();
        PlayerHand player1 = new PlayerHand();
        Random rand = new Random();
        List<PokerCard> communityCards = new ArrayList<>();

        String handtype;
        long millis = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++){
            communityCards.addAll(deck.dealCommunityCards());
            player1.setCommunityCards(communityCards);
            player1.addCard(deck.dealCard());
            player1.addCard(deck.dealCard());

            handtype = player1.getHandName();
            if (statisticMap.containsKey(handtype)) {
                statisticMap.put(handtype, statisticMap.get(handtype) + 1);
            }else {
                statisticMap.put(handtype, 1);
            }

            deck.resetDeck();
            player1.reset();
            communityCards.clear();
        }

        for (String key : statisticMap.keySet()) {
            System.out.printf("%-15s : %d\n", key, statisticMap.get(key));
        }
        System.out.println("Time: " + (System.currentTimeMillis() - millis)/1000.0 + "s");
    }

    public static void test3() {
        List<PokerCard> communityCards = new ArrayList<>(){{
            add(new PokerCard("A", "Hearts"));
            add(new PokerCard("A", "Hearts"));
            add(new PokerCard("3", "Hearts"));
            add(new PokerCard("4", "Hearts"));
            add(new PokerCard("5", "Hearts"));
        }};
        PlayerHand player1 = new PlayerHand();
        player1.setCommunityCards(communityCards);
        player1.addCard(new PokerCard("K", "Hearts"));
        player1.addCard(new PokerCard("9", "Hearts"));
        List<PokerCard> bestHand = player1.buildBestTwoPair();
        for (PokerCard card : bestHand) {
            System.out.println(card);
        }
    }

    public static void test4(){
        Scanner sc = new Scanner(System.in);
        String msg = "";
        Random rand = new Random();
        PokerDeck pokerDeck = new PokerDeck();
        List<PokerCard> communityCards = new ArrayList<>();
        PlayerHand player1 = new PlayerHand();
        player1.setCommunityCards(communityCards);

        do {
            for (int i = 0; i < 10; i++) {
                System.out.println("\n");
            }
            pokerDeck.resetDeck();
            player1.reset();
            communityCards.clear();

            communityCards.addAll(pokerDeck.dealCommunityCards());
            player1.addCard(pokerDeck.dealCard());
            player1.addCard(pokerDeck.dealCard());

            System.out.println("Community Cards: ");
            for (PokerCard card : communityCards) {
                System.out.println(card);
            }
            System.out.println("---------------------------------------------");
            System.out.println("Whole Cards:");
            for (PokerCard card : player1.getPlayerCards()){
                System.out.println(card);
            }
            System.out.println("---------------------------------------------");
            System.out.println(player1.getHandName());
            for (PokerCard card : player1.getBestHand()){
                System.out.println(card);
            }
            msg = sc.nextLine();
        }while (!msg.equals("q"));
    }
}
