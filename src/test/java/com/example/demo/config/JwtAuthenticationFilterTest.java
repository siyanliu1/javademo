package com.example.demo.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.List;

public class JwtAuthenticationFilterTest {

    @Test
    public void testDoFilterInternal_validToken() throws Exception {
        String secret = "secret";
        // Create a token with subject "testuser" and a sample authority
        String token = Jwts.builder()
                .setSubject("testuser")
                .claim("authorities", List.of("PERM_READ"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                .compact();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        SecurityConfig.JwtAuthenticationFilter filter = new SecurityConfig.JwtAuthenticationFilter(secret);
        filter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
