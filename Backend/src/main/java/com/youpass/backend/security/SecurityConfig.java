package com.youpass.backend.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                // disable CSRF because REST API sử dụng Token (JWT)
                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults()) // cho phép các port ở frontend có th gủi request

                // Phân quyền Endpoint
                .authorizeHttpRequests(auth -> auth

                        // Cho phép truy câp tự do vào các API auth ( đăng kí, đăng nhập)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Các API bắt đầu bằng /api/admin/** yêu cầu role ADMIN

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Tất cả các request còn lại bắt buộc phải xác thực
//                        .anyRequest().authenticated()

                        // trong thời gian test endpoint trên postman có thể để như này để test
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    
}
