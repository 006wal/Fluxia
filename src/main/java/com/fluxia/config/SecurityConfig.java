package com.fluxia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) // important pour POST simples (ton login actuel)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login",
                        "/register",
                        "/",
                        "/learn-more",
                        "/css/**",
                        "/js/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable()) // on garde TON login
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
            );

        return http.build();
    }
}
