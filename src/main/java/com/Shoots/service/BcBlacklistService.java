package com.Shoots.service;

import com.Shoots.domain.BcBlacklist;

import java.util.List;
import java.util.Map;

public interface BcBlacklistService {
    List<Map<String, Object>> getBlackListById(Integer business_idx, String block, String unblock);

    void insertBcBlacklist(BcBlacklist bcBlacklist);

    String getStatusById(Object user_idx, int business_idx);

    void updateBcBlacklist(int idx, Integer business_idx);

    boolean isBlockForBusiness(Integer idx, int writer);
}
