package com.Shoots.service;

import com.Shoots.domain.BcBlacklist;
import com.Shoots.mybatis.mapper.BcBlacklistMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BcBlacklistServiceImpl implements BcBlacklistService{

    private BcBlacklistMapper dao;

    public BcBlacklistServiceImpl(BcBlacklistMapper dao) {
        this.dao = dao;
    }

    @Override
    public List<Map<String, Object>> getBlackListById(Integer business_idx, String block, String unblock) {

        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("business_idx", business_idx);
        map.put("block", block);
        map.put("unblock", unblock);

        return dao.getBlackListById(map);
    }

    @Override
    public void insertBcBlacklist(BcBlacklist bcBlacklist) {
        dao.insertBcBlacklist(bcBlacklist);
    }

    @Override
    public String getStatusById(Object user_idx, int business_idx) {
        return dao.getStatusById(user_idx, business_idx);
    }

    @Override
    public void updateBcBlacklist(int idx, Integer business_idx) {
        dao.updateBcBlacklist(idx, business_idx);
    }

    @Override
    public boolean isBlockForBusiness(Integer idx, int writer) {
        return dao.isBlockForBusiness(idx, writer);
    }
}
