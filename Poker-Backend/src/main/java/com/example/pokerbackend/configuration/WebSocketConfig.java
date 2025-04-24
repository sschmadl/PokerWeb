package com.example.pokerbackend.configuration;

import com.example.pokerbackend.handler.GameSessionWebsocketHandler;
import com.example.pokerbackend.handler.GlobalChatWebsocketHandler;
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
        this.gameWebSocketHandler = gameWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameWebSocketHandler, "/game-info-socket")
                .setAllowedOrigins("*");
    }

    @Bean
    public GlobalChatWebsocketHandler myWebSocketHandler() {
        return new GlobalChatWebsocketHandler();
    }
}