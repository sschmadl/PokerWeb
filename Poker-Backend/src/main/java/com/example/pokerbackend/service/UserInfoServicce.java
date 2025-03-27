package com.example.pokerbackend.service;

import com.example.pokerbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServicce {
    private final UserRepository userRepository;

    public UserInfoServicce(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
