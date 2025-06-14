package com.example.pokerbackend.service;

import com.example.pokerbackend.Exceptions.InvalidOldPassword;
import com.example.pokerbackend.Exceptions.InvalidUsernameOrPassword;
import com.example.pokerbackend.Exceptions.UserAlreadyExistsException;
import com.example.pokerbackend.model.User;
import com.example.pokerbackend.repository.UserRepository;
import com.example.pokerbackend.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String authenticate(String username, String password) throws InvalidUsernameOrPassword {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return jwtUtil.generateToken(username);
        }
        throw new InvalidUsernameOrPassword("Invalid username or password");
    }

    public void register(String username, String password) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPasswordChangedDate(new Date());
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public boolean isValidToken(String token){
        return jwtUtil.validateToken(token);
    }

    public void changePassword(String username, String oldPassword, String newPassword) throws Exception{
        User user = userRepository.findUserByUsername(username);
        String currentPassword = user.getPassword();
        if (!passwordEncoder.matches(oldPassword, currentPassword)) {
            throw new InvalidOldPassword("Old password is wrong");
        }else {
            String hashedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedPassword);
            user.setPasswordChangedDate(new Date());
            userRepository.save(user);
        }
    }
}
