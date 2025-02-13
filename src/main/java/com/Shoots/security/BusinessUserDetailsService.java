package com.Shoots.security;

import com.Shoots.domain.BusinessUser;
import com.Shoots.domain.RegularUser;
import com.Shoots.mybatis.mapper.BusinessUserMapper;
import com.Shoots.mybatis.mapper.RegularUserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class BusinessUserDetailsService implements UserDetailsService {
    private static Logger logger = LoggerFactory.getLogger(BusinessUserDetailsService.class);
    private BusinessUserMapper businessUserMapper;

    public BusinessUserDetailsService(BusinessUserMapper businessUserMapper) {
        this.businessUserMapper = businessUserMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        logger.info("id는 로그인시 입력한 값 : " + id);

        BusinessUser businessUser = businessUserMapper.selectById(id);
        logger.info("로그인 한 유저의 role은 ? :" + businessUser.getRole());
        logger.info("로그인 한 기업의 사업자번호는 ? :" + businessUser.getBusiness_number());

        return businessUser;
    }


}
