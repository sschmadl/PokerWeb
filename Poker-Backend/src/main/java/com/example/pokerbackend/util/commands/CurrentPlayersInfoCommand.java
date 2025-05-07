package com.example.pokerbackend.util.commands;

import com.example.pokerbackend.util.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentPlayersInfoCommand {
    String command = "current-players-info";
    List<Map<Object,Object>> players = new ArrayList();

    public CurrentPlayersInfoCommand(List<Player> players) {
        for (Player player : players) {
            Map<Object,Object> map = new HashMap<>();
            map.put("name", player.getName());
            map.put("credits", player.getCredits());
            this.players.add(map);
        }
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List getPlayers() {
        return players;
    }

    public void setPlayers(List players) {
        this.players = players;
    }
}