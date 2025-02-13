package com.Shoots.security;

import com.Shoots.domain.BusinessUser;
import com.Shoots.domain.RegularUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private  static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        String url = "";

        if (principal instanceof RegularUser) {
            RegularUser regularUser = (RegularUser) principal;
            HttpSession session = request.getSession();
            logger.info("Regular User: {}", regularUser.getRole());
            session.setAttribute("idx", regularUser.getIdx());
            session.setAttribute("id", regularUser.getUser_id());
            session.setAttribute("role", regularUser.getRole());
            session.setAttribute("gender", regularUser.getGender());
            session.setAttribute("usertype", "A");


            if(regularUser.getRole().equals("admin")){
                url = request.getContextPath() + "/testAdmin";
            } else{
                url = request.getContextPath()+"/mainBefore";
            }

        } else if (principal instanceof BusinessUser) {
            BusinessUser businessUser = (BusinessUser) principal;
            HttpSession session = request.getSession();
            logger.info("Business User: {}", businessUser.getRole());
            session.setAttribute("idx", businessUser.getBusiness_idx());
            session.setAttribute("id", businessUser.getBusiness_id());
            session.setAttribute("role", businessUser.getRole());
            session.setAttribute("businessAccess", businessUser.getLogin_status());
            session.setAttribute("usertype", "B");


            if (businessUser.getLogin_status().equals("access")) {
                url = request.getContextPath() + "/business/dashboard";
            } else {
                url = request.getContextPath() + "/mainBefore";
            }
            
        }

        // 로그인 성공 후 redirect
        response.sendRedirect(url);
    }
}
