package com.Shoots.service;

import com.Shoots.domain.BusinessInfo;

public interface BusinessInfoService {
    BusinessInfo getInfoById(Integer business_idx);

    void insertBusinessInfo(BusinessInfo businessInfo);

    void updateBusinessInfo(BusinessInfo businessInfo);
}
