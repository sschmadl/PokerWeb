package com.example.pokerbackend.controller;

import com.example.pokerbackend.Exceptions.InvalidOldPassword;
import com.example.pokerbackend.Exceptions.InvalidUsernameOrPassword;
import com.example.pokerbackend.Exceptions.UserAlreadyExistsException;
import com.example.pokerbackend.service.AuthService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            authService.register(request.getUsername(), request.getPassword());
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        }
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        String token;
        try {
            token = authService.authenticate(request.getUsername(), request.getPassword());
        } catch (InvalidUsernameOrPassword e) {
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        }
        return new ResponseEntity<>(new AuthResponse(token),HttpStatusCode.valueOf(200));
    }

    @PostMapping("/is-valid-token")
    public ResponseEntity<?> isValidToken(@RequestBody ValidTokenRequest request) {
        if (authService.isValidToken(request.getToken())){
            return new ResponseEntity<>(HttpStatusCode.valueOf(200));
        }else {
            return new ResponseEntity<>(new ErrorResponse("invalid token"), HttpStatusCode.valueOf(400));
        }
    }

    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
            return new ResponseEntity<>(HttpStatusCode.valueOf(200));
        }catch (InvalidOldPassword e){
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(400).body(new ErrorResponse("Alles Kaputt"));
        }
    }
}

class AuthRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

class ValidTokenRequest{
    private String token;
    public ValidTokenRequest(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}

class ErrorResponse{
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

class ChangePasswordRequest{
    String oldPassword;
    String newPassword;
    String username;
    public ChangePasswordRequest(String oldPassword, String newPassword, String username) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}