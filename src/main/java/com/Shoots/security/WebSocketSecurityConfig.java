package com.Shoots.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(2) // RegularUserSecurityConfig(1)보다 낮은 우선순위
public class WebSocketSecurityConfig {

    @Bean
    public SecurityFilterChain webSocketSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/livechat/livechat/ws/**") // WebSocket 요청을 위한 보안 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/livechat/livechat/ws/**").permitAll() // WebSocket 요청 허용
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/livechat/livechat/ws/**")); // WebSocket은 CSRF 예외 처리

        return http.build();
    }
}
