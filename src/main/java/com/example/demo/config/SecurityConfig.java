package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
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
import java.util.List;
import java.util.stream.Collectors;

@Configuration("userSecurityConfig") // 指定不同的 bean 名称
@EnableMethodSecurity  // 启用 @PreAuthorize 等注解
public class SecurityConfig {

    @Value("${jwt.secret:secret}")
    private String jwtSecret;

    @Bean(name = "userFilterChain")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()  // 允许认证服务先登录
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtSecret), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    // 内部 JwtAuthenticationFilter 类：从请求头中解析 token 并设置认证信息
    public static class JwtAuthenticationFilter extends org.springframework.web.filter.OncePerRequestFilter {
        private final String jwtSecret;
        public JwtAuthenticationFilter(String jwtSecret) {
            this.jwtSecret = jwtSecret;
        }
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    Claims claims = Jwts.parser()
                            .setSigningKey(jwtSecret.getBytes())
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
                    SecurityContextHolder.clearContext();
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
