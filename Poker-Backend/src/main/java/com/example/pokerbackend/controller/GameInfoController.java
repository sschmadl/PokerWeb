package com.example.pokerbackend.controller;

import com.example.pokerbackend.util.GameInfo;
import com.example.pokerbackend.util.GameSessionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/game-info")
public class GameInfoController {

    @GetMapping("/existing-games")
    public ResponseEntity<List<GameInfo>> getExistingGames(){
        GameSessionManager sessionManager = GameSessionManager.getInstance();
        return ResponseEntity.ok(sessionManager.getGameSessionInfos());
    }
}
