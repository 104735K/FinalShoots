package com.Shoots;


import com.Shoots.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@Order(2)
public class BusinessUserSecurityConfig {

    private DataSource dataSource;
    private LoginSuccessHandler loginSuccessHandler;
    private LoginFailHandler loginFailHandler;
    private BusinessUserDetailsService businessUserDetailsService;
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    public BusinessUserSecurityConfig(DataSource dataSource, LoginSuccessHandler loginSuccessHandler, LoginFailHandler loginFailHandler, BusinessUserDetailsService businessUserDetailsService, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.dataSource = dataSource;
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailHandler = loginFailHandler;
        this.businessUserDetailsService = businessUserDetailsService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public BCryptPasswordEncoder businessUserPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider businessUserAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(businessUserDetailsService);
        provider.setPasswordEncoder(businessUserPasswordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain businessUserFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/businessLoginProcess/**", "/businessLogin", "/business/**")
                .formLogin(fo -> fo
                        .loginPage("/businessLogin")
                        .loginProcessingUrl("/businessLoginProcess")
                        .usernameParameter("business_id")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailHandler)
                )
                .authenticationProvider(businessUserAuthenticationProvider()) // BusinessUser Provider
                .logout(lo -> lo
                        .logoutSuccessUrl("/businessLogin")
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("remember-me", "JSESSION_ID")
                )
                .authorizeHttpRequests(au -> au
                        .requestMatchers("/business/**", "/inquiry/**").hasAuthority("business")
                        .requestMatchers("/**").permitAll()
                )
                .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler)
                );


        return http.build();
    }

}
