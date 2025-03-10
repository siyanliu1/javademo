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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.DispatcherType;
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

    static {
        // 采用可继承的 ThreadLocal 策略，确保子线程能继承安全上下文
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    // 从配置文件中读取 Base64 编码后的密钥（请确保测试和生产环境均配置正确）
    @Value("${jwt.secret:defaultBase64Secret==}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // 设置无状态会话
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 对 ASYNC 与 ERROR 分派直接放行
                        .dispatcherTypeMatchers(DispatcherType.ASYNC, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/login", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                // 添加 JWT 过滤器到 UsernamePasswordAuthenticationFilter 之前
                .addFilterBefore(new JwtAuthenticationFilter(jwtSecret), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // 提供 AuthenticationManager，用于控制器注入
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * JwtAuthenticationFilter 实现：
     * - 对初始请求：解析 Authorization header 中的 token，并将 SecurityContext 保存到 request 属性中
     * - 对异步请求（DispatcherType.ASYNC）：从 request 属性中恢复 SecurityContext
     */
    public static class JwtAuthenticationFilter extends org.springframework.web.filter.OncePerRequestFilter {
        private final String jwtSecret;

        public JwtAuthenticationFilter(String jwtSecret) {
            this.jwtSecret = jwtSecret;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            System.out.println("[JwtAuthenticationFilter] DispatcherType: " + request.getDispatcherType());

            // 如果是异步分派，则尝试从 request 属性中恢复 SecurityContext
            if (request.getDispatcherType() == DispatcherType.ASYNC) {
                Object storedContext = request.getAttribute("SECURITY_CONTEXT");
                if (storedContext instanceof SecurityContext) {
                    SecurityContextHolder.setContext((SecurityContext) storedContext);
                    System.out.println("[JwtAuthenticationFilter] ASYNC dispatch: restored SecurityContext: "
                            + SecurityContextHolder.getContext().getAuthentication());
                }
                filterChain.doFilter(request, response);
                return;
            }

            // 对于初始请求（DispatcherType.REQUEST 等），如果当前 SecurityContext 为空，则解析 token
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String header = request.getHeader("Authorization");
                System.out.println("[JwtAuthenticationFilter] Authorization header: " + header);
                if (header != null && header.startsWith("Bearer ")) {
                    String token = header.substring(7);
                    try {
                        // 对 Base64 编码的密钥进行解码
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
                        // 保存 SecurityContext 到 request 属性，供异步调度时恢复
                        request.setAttribute("SECURITY_CONTEXT", SecurityContextHolder.getContext());
                        System.out.println("[JwtAuthenticationFilter] Authentication set: " + SecurityContextHolder.getContext().getAuthentication());
                    } catch (Exception e) {
                        System.err.println("[JwtAuthenticationFilter] JWT解析失败: " + e.getMessage());
                        SecurityContextHolder.clearContext();
                    }
                }
            }
            filterChain.doFilter(request, response);
            System.out.println("[JwtAuthenticationFilter] After filterChain, SecurityContext = " + SecurityContextHolder.getContext().getAuthentication());
        }
    }
}
