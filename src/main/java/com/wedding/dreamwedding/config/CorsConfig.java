package com.wedding.dreamwedding.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Replace with your real production frontend domain when ready
                .allowedOrigins("http://localhost:3000", "https://dreamwedding.lk", "https://www.dreamwedding.lk")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight response for 1 hour
    }
}
