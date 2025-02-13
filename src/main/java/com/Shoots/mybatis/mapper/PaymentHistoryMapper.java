package com.Shoots.mybatis.mapper;

import com.Shoots.domain.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentHistoryMapper {
    void insertHistory(Payment payment);
}
