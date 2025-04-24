package com.example.pokerbackend.configuration;

import com.example.pokerbackend.handler.GameSessionWebsocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final GameSessionWebsocketHandler gameWebSocketHandler;

    public WebSocketConfig(GameSessionWebsocketHandler gameWebSocketHandler) {
        System.out.println("websocket config created");
        this.gameWebSocketHandler = gameWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.out.println("websocket registered");
        registry.addHandler(gameWebSocketHandler, "/ws/game-info-socket")
                .setAllowedOrigins("*");
    }

    @Bean
    public String sanityCheckBean() {
        System.out.println("WebSocketConfig is being loaded!");
        return "ok";
    }
}