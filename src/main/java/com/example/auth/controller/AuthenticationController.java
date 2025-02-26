package com.example.auth.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Value("${jwt.secret:secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpirationInMs;

    public AuthenticationController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        logger.info("Received login request for username: {}", username);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            logger.info("User {} authenticated successfully", username);

            List<String> authorities = authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList());

            String token = Jwts.builder()
                    .setSubject(username)
                    .claim("authorities", authorities)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                    .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                    .compact();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully Authenticated");
            response.put("accessToken", token);
            logger.info("Generated token for user {}: {}", username, token.substring(0, 10) + "...");
            return response;
        } catch (Exception e) {
            logger.error("Authentication failed for user {}: {}", username, e.getMessage());
            throw e;
        }
    }
}
