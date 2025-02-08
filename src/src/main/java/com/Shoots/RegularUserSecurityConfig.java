package com.Shoots;


import com.Shoots.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
@Order(1)
public class RegularUserSecurityConfig {

    private DataSource dataSource;
    private LoginSuccessHandler loginSuccessHandler;
    private LoginFailHandler loginFailHandler;
    private RegularUserDetailsService regularUserDetailsService;
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    public RegularUserSecurityConfig(DataSource dataSource, LoginSuccessHandler loginSuccessHandler, LoginFailHandler loginFailHandler, RegularUserDetailsService regularUserDetailsService, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.dataSource = dataSource;
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailHandler = loginFailHandler;
        this.regularUserDetailsService = regularUserDetailsService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    @Primary
    public BCryptPasswordEncoder regularUserPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        //영구 토큰 저장/인증 하는 클래스. 주로 '아이디 기억하기' 기능 등에 사용됨.
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource); // import javax.sql.DataSource
        return jdbcTokenRepository;
    }

    @Bean
    public AuthenticationProvider regularUserAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(regularUserDetailsService);
        provider.setPasswordEncoder(regularUserPasswordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain regularUserFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/login/**", "/loginProcess/**", "/logout/**", "/inquiry/**", "/testAdmin/**")
                .formLogin(fo -> fo
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProcess")
                        .usernameParameter("id")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailHandler)
                )
                .authenticationProvider(regularUserAuthenticationProvider()) // RegularUser Provider
                .logout(lo -> lo
                        .logoutSuccessUrl("/login")
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("remember-me", "JSESSION_ID")
                )
                .authorizeHttpRequests(au -> au
                        .requestMatchers("/testAdmin/**").hasAuthority("admin")
                        .requestMatchers("/inquiry/**").hasAnyAuthority("admin", "common", "business")
                        .requestMatchers("/**").permitAll()
                )
                .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

}
