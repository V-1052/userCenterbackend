package com.yanshui.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String frontendOrigin = System.getenv("FRONTEND_ORIGIN");
                if (frontendOrigin == null || frontendOrigin.isBlank()) {
                    frontendOrigin = "http://localhost:3000";
                }
                registry.addMapping("/**")
                        .allowedOrigins(frontendOrigin)
                        .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
                        .allowedHeaders("Content-Type","Authorization","X-Requested-With")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
