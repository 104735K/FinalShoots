package com.Shoots.mybatis.mapper;

import com.Shoots.domain.Payment;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentMapper {
    void insertPayment(Payment payment);

    boolean hasPaidForMatch(int idx, int match_idx);

    int getPlayerCount(int match_idx);

    List<Map<String, Object>> getPaymentListById(Integer idx);

    Payment getPaymentInfoById(Integer idx, int match_idx);

    void updatePayment(int payment_idx);

    List<Payment> getPaymentListByMatchIdx(int match_idx);

    List<Map<String, Object>> getPlayerCountByMonth();

    List<HashMap<String, Object>> userPaymentList(int id); // 사용자 id로 결제(매칭) 기록을 가져온다

    int getPaymentCount(int id);

    List<Map<String, Object>> getPlayerCountByMonth(Integer business_idx);

    List<Map<String, Object>> getUserPaymentListByMatchIdx(int match_idx);

}
