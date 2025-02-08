package com.Shoots.service;

import com.Shoots.domain.Payment;
import com.Shoots.mybatis.mapper.PaymentHistoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentHistoryServiceImpl implements PaymentHistoryService{

    private PaymentHistoryMapper paymentHistoryMapper;

    @Override
    public void insertHistory(Payment payment) {
        paymentHistoryMapper.insertHistory(payment);
    }
}
