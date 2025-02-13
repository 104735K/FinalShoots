package com.Shoots.security;

import com.Shoots.domain.RegularUser;
import com.Shoots.mybatis.mapper.RegularUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class RegularUserDetailsService implements UserDetailsService {
    private static Logger logger = LoggerFactory.getLogger(RegularUserDetailsService.class);
    private RegularUserMapper regularUserMapper;

    public RegularUserDetailsService(RegularUserMapper regularUserMapper) {
        this.regularUserMapper = regularUserMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        logger.info("id는 로그인시 입력한 값 : " + id);

        RegularUser regularUser = regularUserMapper.selectById(id);
        logger.info("로그인 한 유저의 role은 ? :" + regularUser.getRole());
        logger.info("로그인 한 유저의 주민번호는 ? :" + regularUser.getJumin());

        if(regularUser == null) {
            logger.info("id : " + id + "가 없습니다.");

            throw new UsernameNotFoundException("id : " + id + "가 없습니다.");
        }
        return regularUser;
    }

}
