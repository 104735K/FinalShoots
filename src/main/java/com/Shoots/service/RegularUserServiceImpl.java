package com.Shoots.service;

import com.Shoots.domain.RegularUser;
import com.Shoots.mybatis.mapper.RegularUserMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegularUserServiceImpl implements RegularUserService {

    private RegularUserMapper regularUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public RegularUserServiceImpl(RegularUserMapper regularUserMapper, BCryptPasswordEncoder passwordEncoder) {
        this.regularUserMapper = regularUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (passwordEncoder == null) {
            System.out.println("passwordEncoder가 주입 안된 상태입니다. : passwordEncoder = null");
        }
    }

    @Override
    public int selectById(String id) {
        RegularUser user = regularUserMapper.selectById(id);
        return (user == null) ? -1 : 1;//-1은 아이디가 존재x, 1은 아이디가 존재o
    }

    public RegularUser selectWithId(String id) {
        RegularUser user = regularUserMapper.selectById(id);
        return user;
    }

    @Override
    public RegularUser findByKakaoUserId(String kakaoId) {
        RegularUser user = regularUserMapper.findByKakaoUserId(kakaoId);
        return user;
    }

    @Override
    public RegularUser findByGoogleUserId(String googleAuId) {
        RegularUser user = regularUserMapper.findByGoogleUserId(googleAuId);
        return user;
    }

    @Override
    public RegularUser findByNaverAuId(String naverAuId) {
        RegularUser user = regularUserMapper.findByNaverAuId(naverAuId);
        return user;
    }

    @Override
    public int selectByIdPassword(String id, String password) {
        RegularUser user = regularUserMapper.selectById(id);

        if (user == null) {
            return -1; //아이디가 없는 경우
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return 0; //아이디는 있는데 비밀번호 일치X
        }

        return 1;  //아이디와 비밀번호가 일치하는 경우
    }


    @Override
    public int insert(RegularUser user) {
        return regularUserMapper.insert(user);
    }

    @Override
    public int insert2(RegularUser user) {return regularUserMapper.insert2(user);}

    @Override
    public int insert3(RegularUser user) {return regularUserMapper.insert3(user);}

    @Override
    public int selectByEmail(String email) {
        RegularUser user = regularUserMapper.selectByEmail(email);
        return (user == null) ? -1 : 1;//-1은 아이디가 존재x, 1은 아이디가 존재o
    }

    @Override
    public RegularUser findIdWithEmail(String email) {
        RegularUser user = regularUserMapper.findIdWithEmail(email);
        return user;
    }

    @Override
    public RegularUser selectWithIdAndEmail(String user_id, String email) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user_id", user_id);
        hashMap.put("email", email);
        RegularUser user = regularUserMapper.selectWithIdAndEmail(hashMap);
        return user;
    }

    @Override
    public int updateRegularUserPassword(RegularUser user) {
        return regularUserMapper.updateRegularUserPassword(user);
    }

    public List<Map<String, Object>> getUserListForBusiness(Integer business_idx, String vip, Integer
            gender, String age) {
        return regularUserMapper.getUserListForBusiness(business_idx, vip, gender, age);
    }

    @Override
    public int listCount(String search_word) {
        Map<String, Object> map = new HashMap<>();
        if(!search_word.isEmpty()){
            map.put("search_word", "%" + search_word + "%");
        }
        return regularUserMapper.listCount(map);
    }

    @Override
    public List<RegularUser> getUserList(String search_word, int page, int limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        if(!search_word.isEmpty()){
            map.put("search_word", "%" + search_word + "%");
        }

        int offset = (page - 1) * limit;
        map.put("offset", offset);
        int pageSize = limit;
        map.put("pageSize", pageSize);
        return regularUserMapper.getUserList(map);
    }

    @Override
    public void setCommonUser(int id) {
        regularUserMapper.setCommonUser(id);
    }

    @Override
    public void setAdminUser(int id) {
        regularUserMapper.setAdminUser(id);
    }

    @Override
    public String getEmail(int id) {
        return regularUserMapper.getEmail(id);
    }

    @Override
    public RegularUser regularUserList(String id) {
        return regularUserMapper.regularUserList(id);
    }

    @Override
    public void updateRegularUser(RegularUser user) {
        regularUserMapper.updateRegularUser(user);
    }

    @Override
    public List<Map<String, Object>> getRegularUser() {
        return regularUserMapper.getUserCount();
    }

    @Override
    public int allUsers() {
        return regularUserMapper.allUsers();
    }

    @Override
    public Map<String, Object>  getPlayerGenderCount(Integer business_idx) {

        Map<String, Object> result = regularUserMapper.getPlayerGenderCount(business_idx);

        if (result == null) {
            result = new HashMap<>();
        }

        Map<String, Object> genderCount = new HashMap<>();

        int gender1 = ((Number) result.getOrDefault("gender_1_count", 0)).intValue();
        int gender2 = ((Number) result.getOrDefault("gender_2_count", 0)).intValue();
        int gender3 = ((Number) result.getOrDefault("gender_3_count", 0)).intValue();
        int gender4 = ((Number) result.getOrDefault("gender_4_count", 0)).intValue();

        genderCount.put("group_1_3", gender1 + gender3);

        genderCount.put("group_2_4", gender2 + gender4);

        return genderCount;
    }

    @Override
    public int deleteRegularUser(int idx) {
        return regularUserMapper.deleteRegularUser(idx);
    }
}

