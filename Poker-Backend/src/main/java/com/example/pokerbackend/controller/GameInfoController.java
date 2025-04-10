package com.example.pokerbackend.controller;

import com.example.pokerbackend.util.GameInfo;
import com.example.pokerbackend.util.GameSessionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game-info")
public class GameInfoController {

    @GetMapping("/existing-games")
    public ResponseEntity<Map<String, List<GameInfo>>> getExistingGames(){
        GameSessionManager sessionManager = GameSessionManager.getInstance();
        Map<String, List<GameInfo>> map = new HashMap<>();
        map.put("games", sessionManager.getGameSessionInfos());
        return ResponseEntity.ok(map);
    }
}
