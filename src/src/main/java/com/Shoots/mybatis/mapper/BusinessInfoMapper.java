package com.Shoots.mybatis.mapper;

import com.Shoots.domain.BusinessInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BusinessInfoMapper {
    BusinessInfo getInfoById(Integer business_idx);

    void insertBusinessInfo(BusinessInfo businessInfo);

    void updateBusinessInfo(BusinessInfo businessInfo);
}
