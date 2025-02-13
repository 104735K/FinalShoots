package com.Shoots.mybatis.mapper;

import com.Shoots.domain.BusinessUser;
import com.Shoots.domain.RegularUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface BusinessUserMapper {
    public BusinessUser selectById(String id);
    public int insert(BusinessUser user);
    public BusinessUser selectByEmail(String email);
    public BusinessUser findIdWithEmail(String email);
    public BusinessUser selectWithIdAndEmail(HashMap<String, Object> map);
    public int updateBusinessUserPassword(BusinessUser user);
    public List<BusinessUser> getList(Map<String, Object> map);
    public int listCount(Map<String, Object> map);
    public void approveStatus(int id);
    public void refuseStatus(int id);
    public List<BusinessUser> getApprovedList(Map<String, Object> map);
    public int listApprovedCount(Map<String, Object> map);
    public String getEmail(int id);
    BusinessUser getBusinessUserInfoById(Integer business_idx);

    BusinessUser getBusinessUserAddressById(String business_id);

    List<Integer> getAllBusinessIndexes();

    List<BusinessUser> getBusinessNames(List<Integer> businessIdxList);

    String getAddressByBusinessIdx(int businessIdx);

    List<BusinessUser> getListForLocation(Map<String, Object> map);

    Map<String, Integer> getLocationByCoordinates(String first, String second, String third);

    List<Map<String, Object>> getBusinessUsers();
}
