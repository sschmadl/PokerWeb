package com.example.pokerbackend.configuration;

import com.example.pokerbackend.handler.GlobalChatWebsocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GlobalChatWebsocketHandler(), "/ws")
                .setAllowedOrigins("*");
    }

    @Bean
    public GlobalChatWebsocketHandler myWebSocketHandler() {
        return new GlobalChatWebsocketHandler();
    }
}