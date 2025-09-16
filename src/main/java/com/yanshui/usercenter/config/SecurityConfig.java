package com.yanshui.usercenter.config;

// src/main/java/com/yanshui/usercenter/config/SecurityConfig.java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/user/login", "/user/register",
                        "/error", "/user/search", "/user/current",
                        "/user/update", "/user/delete", "user/logout").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }
}
