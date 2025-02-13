package com.Shoots;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.naver.com");  // SMTP 서버 주소 설정
        mailSender.setPort(587);  // 포트 설정
        mailSender.setUsername("chldudtn0206@naver.com");
        mailSender.setPassword("dudtn0206");
        return mailSender;
    }
}

