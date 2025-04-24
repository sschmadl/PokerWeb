package com.example.pokerbackend.handler;

import com.example.pokerbackend.service.PlayerFactoryService;
import com.example.pokerbackend.util.GameSessionManager;
import com.example.pokerbackend.util.JwtUtil;
import com.example.pokerbackend.util.Player;
import com.example.pokerbackend.util.QueryUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Map;

@Component
public class GameSessionWebsocketHandler extends TextWebSocketHandler {
    private final PlayerFactoryService playerFactory;
    private final JwtUtil jwtUtil;
    private GameSessionManager gameSessionManager = GameSessionManager.getInstance();

    public GameSessionWebsocketHandler(PlayerFactoryService playerFactory, JwtUtil jwtUtil) {
        this.playerFactory = playerFactory;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        URI uri = session.getUri();
        String query = uri.getQuery();

        Map<String, String> queryParams = QueryUtils.splitQuery(query);
        // Close Session if token isn't passed in url
        if ( !queryParams.containsKey("token")){
            System.out.println("Session closed: No token passed from " + session.getRemoteAddress().getAddress().getHostAddress());
            session.close();
            return;
        }
        String token = queryParams.get("token");
        System.out.println("Session opened: " + session.getRemoteAddress().getAddress().getHostAddress());

        if ( !jwtUtil.validateToken(token)){
            System.out.println("Session closed: Invalid token passed from " + session.getRemoteAddress().getAddress().getHostAddress());
            session.close();
            return;
        }

        String username = jwtUtil.extractUsername(token);
        System.out.println("Session opened: " + username);

        session.getAttributes().put("username", username);
        Player player = playerFactory.createPlayer(username);
        gameSessionManager.addPlayer(player);

        session.sendMessage(new TextMessage("Welcome " + username));
    }
}
