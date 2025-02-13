package com.Shoots.service;

import com.Shoots.domain.BusinessUser;
import com.Shoots.mybatis.mapper.BusinessUserMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BusinessUserServiceImpl implements BusinessUserService {

    private BusinessUserMapper businessUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public BusinessUserServiceImpl(BusinessUserMapper BusinessUserMapper, @Qualifier("businessUserPasswordEncoder") BCryptPasswordEncoder passwordEncoder) {
        this.businessUserMapper = BusinessUserMapper;
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
        BusinessUser user = businessUserMapper.selectById(id);
        return (user == null) ? -1 : 1;//-1은 아이디가 존재x, 1은 아이디가 존재o
    }

    public BusinessUser selectWithId(String id) {
        BusinessUser user = businessUserMapper.selectById(id);
        return user;
    }


    @Override
    public int selectByIdPassword(String id, String password) {
        BusinessUser user = businessUserMapper.selectById(id);

        if (user == null) {
            return -1; //아이디가 없는 경우
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return 0; //아이디는 있는데 비밀번호 일치X
        }

        return 1;  //아이디와 비밀번호가 일치하는 경우
    }


    @Override
    public int insert(BusinessUser user) {
        return businessUserMapper.insert(user);
    }

    @Override
    public int selectByEmail(String email) {
        BusinessUser user = businessUserMapper.selectByEmail(email);
        return (user == null) ? -1 : 1;//-1은 아이디가 존재x, 1은 아이디가 존재o
    }

    @Override
    public BusinessUser findIdWithEmail(String email) {
        BusinessUser user = businessUserMapper.findIdWithEmail(email);
        return user;
    }

    @Override
    public BusinessUser selectWithIdAndEmail(String business_id, String email) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("business_id", business_id);
        hashMap.put("email", email);
        BusinessUser user = businessUserMapper.selectWithIdAndEmail(hashMap);
        return user;
    }

    @Override
    public int updateBusinessUserPassword(BusinessUser user) {
        return businessUserMapper.updateBusinessUserPassword(user);
    }

    @Override
    public List<BusinessUser> getList(String search_word, int page, int limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        if(!search_word.isEmpty()){
            map.put("search_word", "%" + search_word + "%");
        }
        int offset = (page - 1) * limit;
        map.put("offset", offset);
        int pageSize = limit;
        map.put("pageSize", pageSize);
        return businessUserMapper.getList(map);
    }

    @Override
    public int listCount(String search_word) {
        Map<String, Object> map = new HashMap<>();
        if(!search_word.isEmpty()){
            map.put("search_word", "%" + search_word + "%");
        }
        return businessUserMapper.listCount(map);
    }

    @Override
    public void approveStatus(int id) {
    businessUserMapper.approveStatus(id);
    }

    @Override
    public void refuseStatus(int id) {
        businessUserMapper.refuseStatus(id);
    }

    @Override
    public List<BusinessUser> getApprovedList(String search_word, int page, int limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        if(!search_word.isEmpty()){
            map.put("search_word", "%" + search_word + "%");
        }
        int offset = (page - 1) * limit;
        map.put("offset", offset);
        int pageSize = limit;
        map.put("pageSize", pageSize);
        return businessUserMapper.getApprovedList(map);
    }

    @Override
    public int listApprovedCount(String search_word) {
        Map<String, Object> map = new HashMap<>();
        if(!search_word.isEmpty()){
            map.put("search_word", "%" + search_word + "%");
        }
        return businessUserMapper.listApprovedCount(map);
    }

    @Override
    public String getEmail(int id) {
        return businessUserMapper.getEmail(id);
    }

    @Override
    public BusinessUser getBusinessUserInfoById(Integer business_idx) {
        return businessUserMapper.getBusinessUserInfoById(business_idx);
    }

    @Override
    public BusinessUser getBusinessUserAddressById(String business_id) {
        return businessUserMapper.getBusinessUserAddressById(business_id);
    }

    @Override
    public List<Integer> getAllBusinessIndexes() {
        return businessUserMapper.getAllBusinessIndexes();
    }

    @Override
    public Map<Integer, String> getBusinessNames(List<Integer> businessIdxList) {
        List<BusinessUser> businesses = businessUserMapper.getBusinessNames(businessIdxList);
        return businesses.stream()
                .collect(Collectors.toMap(BusinessUser::getBusiness_idx, BusinessUser::getBusiness_name));
    }

    @Override
    public List<BusinessUser> getListForLocation(String search_word, int page, int limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        if(!search_word.isEmpty()){
            map.put("search_word", "%" + search_word + "%");
        }
        int offset = (page - 1) * limit;
        map.put("offset", offset);
        int pageSize = limit;
        map.put("pageSize", pageSize);
        return businessUserMapper.getListForLocation(map);
    }

    @Override
    public List<Map<String, Object>> getBusinessUsers() {
        return businessUserMapper.getBusinessUsers();
    }
}
