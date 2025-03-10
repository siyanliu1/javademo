package com.example.demo.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class JwtAuthenticationFilterTest {

    @Test
    public void testDoFilterInternal_validToken() throws Exception {
        // Generate a secure key for HS512 (512 bits)
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        byte[] keyBytes = key.getEncoded();
        // Encode key bytes as Base64 string
        String base64Secret = Base64.getEncoder().encodeToString(keyBytes);

        // Create a token with subject "testuser" and a sample authority
        String token = Jwts.builder()
                .setSubject("testuser")
                .claim("authorities", List.of("PERM_READ"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // Create and invoke the filter using the Base64-encoded secret.
        SecurityConfig.JwtAuthenticationFilter filter = new SecurityConfig.JwtAuthenticationFilter(base64Secret);
        filter.doFilter(request, response, chain);

        // Expect that the authentication is set by our filter.
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
