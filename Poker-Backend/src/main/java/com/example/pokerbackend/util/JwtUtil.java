package com.example.pokerbackend.util;

import com.example.pokerbackend.model.User;
import com.example.pokerbackend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private String secretKey = "FortniteBalls19DollarFortniteCardFemboyThighs";
    private long expirationMs = 86400000; // 24 hours
    private UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String username, Date passwordChangedDate) {
        return Jwts.builder()
                .setSubject(username)
                .claim("passwordChangedDate", String.valueOf(passwordChangedDate.getTime()))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            System.out.println("validate");
            User user = userRepository.findUserByUsername(extractUsername(token));
            System.out.println(user.getUsername());
            if (user == null) return false;
            System.out.println(user.getPasswordChangedDate().getTime());
            System.out.println(extractClaim(token, "passwordChangedDate"));
            System.out.println(Long.parseLong(extractClaim(token, "passwordChangedDate")));
            System.out.println(user.getPasswordChangedDate().getTime() == Long.parseLong(extractClaim(token, "passwordChangedDate")));
            return user.getPasswordChangedDate().getTime() == Long.parseLong(extractClaim(token, "passwordChangedDate"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String extractClaim(String token, String claimKey) {
        Claims claims = extractAllClaims(token);
        return claims.get(claimKey, String.class); // Extrahieren des spezifischen Claims
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
