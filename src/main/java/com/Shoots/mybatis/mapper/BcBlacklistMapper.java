package com.Shoots.mybatis.mapper;

import com.Shoots.domain.BcBlacklist;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface BcBlacklistMapper {
    List<Map<String, Object>> getBlackListById(HashMap<String, Object> map);

    void insertBcBlacklist(BcBlacklist bcBlacklist);

    String getStatusById(Object user_idx, int business_idx);

    void updateBcBlacklist(int idx, Integer business_idx);

    boolean isBlockForBusiness(Integer idx, int writer);
}
