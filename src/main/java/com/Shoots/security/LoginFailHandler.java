package com.Shoots.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class LoginFailHandler implements AuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginFailHandler.class);



    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        HttpSession session = request.getSession();


        logger.info(exception.getMessage());
        logger.info("로그인 실패");
        session.setAttribute("loginResult", "로그인에 실패했습니다. 아이디나 비밀번호를 확인해 주세요");
        logger.info(session.getAttribute("loginResult").toString());

        String url = request.getContextPath() + "/login";
        response.sendRedirect(url);
    }
}
