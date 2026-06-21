package com.wedding.dreamwedding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Enforce CORS configuration from CorsConfig
            .cors(Customizer.withDefaults())
            
            // 2. Protect endpoints strictly by role
            .authorizeHttpRequests(auth -> auth
                // Public paths (landing page assets, Swagger API documentation, Login/Register)
                .requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                
                // Protected Paths
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/bookings/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/budget/**").hasAnyRole("USER", "ADMIN")
                
                // Lock down everything else
                .anyRequest().authenticated()
            )
            
            // 3. Recommended Security Headers to prevent clickjacking & XSS
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self';"))
                .frameOptions(frame -> frame.deny())
            )
            
            // 4. Configure Authentication mechanism (e.g., HTTP Basic or JWT filter hook)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // Fixes Critical Issue #3: Injects safe password hashing mechanics
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength factor 12 provides robust protection against brute force
    }
}
