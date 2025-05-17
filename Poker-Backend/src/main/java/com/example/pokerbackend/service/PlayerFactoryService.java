package com.example.pokerbackend.service;

import com.example.pokerbackend.model.User;
import com.example.pokerbackend.repository.UserRepository;
import com.example.pokerbackend.util.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerFactoryService {
    private final UserRepository userRepository;

    public PlayerFactoryService(UserRepository userRepository) {
        System.out.println("PlayerFactoryService created");
        this.userRepository = userRepository;
    }

    public Player createPlayer(String name) {
        // Fetch user from DB
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a Player with the credits from DB
        return new Player(name, user.getCredits(), userRepository);
    }
}