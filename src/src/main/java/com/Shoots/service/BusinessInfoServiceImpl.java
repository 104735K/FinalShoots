package com.Shoots.service;

import com.Shoots.domain.BusinessInfo;
import com.Shoots.mybatis.mapper.BusinessInfoMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class BusinessInfoServiceImpl implements BusinessInfoService {

    private BusinessInfoMapper dao;

    public BusinessInfoServiceImpl(BusinessInfoMapper dao) {
        this.dao = dao;
    }

    @Override
    public BusinessInfo getInfoById(Integer business_idx) {
        return dao.getInfoById(business_idx);
    }

    @Override
    public void insertBusinessInfo(BusinessInfo businessInfo) {
        dao.insertBusinessInfo(businessInfo);
    }

    @Override
    public void updateBusinessInfo(BusinessInfo businessInfo) {
        dao.updateBusinessInfo(businessInfo);
    }
}
