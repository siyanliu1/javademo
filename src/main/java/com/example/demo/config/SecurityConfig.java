package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // The jwt.secret property should be set to a Base64-encoded string. For testing, we use a default.
    @Value("${jwt.secret:defaultBase64Secret==}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // Use stateless sessions for JWT-based authentication.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()  // Public login endpoint
                        .anyRequest().authenticated()
                )
                // Add our JWT filter before the UsernamePasswordAuthenticationFilter.
                .addFilterBefore(new JwtAuthenticationFilter(jwtSecret), UsernamePasswordAuthenticationFilter.class);
        // Do NOT configure httpBasic() so that it does not interfere with our test authentication.
        return http.build();
    }

    // Expose AuthenticationManager for injection into controllers.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * JwtAuthenticationFilter:
     * If there is no current authentication (for example, set by @WithMockUser),
     * and if the request has a Bearer token, it attempts to validate the token and set authentication.
     */
    public static class JwtAuthenticationFilter extends org.springframework.web.filter.OncePerRequestFilter {
        private final String jwtSecret;
        public JwtAuthenticationFilter(String jwtSecret) {
            this.jwtSecret = jwtSecret;
        }
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            // Only process if there is no existing authentication (e.g. provided by @WithMockUser)
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String header = request.getHeader("Authorization");
                if (header != null && header.startsWith("Bearer ")) {
                    String token = header.substring(7);
                    try {
                        // Decode the Base64-encoded secret.
                        byte[] secretBytes = Base64.getDecoder().decode(jwtSecret);
                        Claims claims = Jwts.parser()
                                .setSigningKey(secretBytes)
                                .parseClaimsJws(token)
                                .getBody();
                        String username = claims.getSubject();
                        List<?> auths = claims.get("authorities", List.class);
                        List<SimpleGrantedAuthority> authorities = (auths != null)
                                ? auths.stream()
                                .map(auth -> new SimpleGrantedAuthority(String.valueOf(auth)))
                                .collect(Collectors.toList())
                                : List.of();
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } catch (Exception e) {
                        // If token validation fails, simply clear the context.
                        SecurityContextHolder.clearContext();
                    }
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
