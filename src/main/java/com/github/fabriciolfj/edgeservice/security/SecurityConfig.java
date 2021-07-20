package com.github.fabriciolfj.edgeservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {
        return http
                .authorizeExchange(e -> e.anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .build();
    }
}
