package com.Shoots.service;

import com.Shoots.domain.Payment;
import com.Shoots.domain.RegularUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    void insertPayment(Payment payment);

    boolean hasPaidForMatch(int idx, int matchIdx);

    int getPlayerCount(int matchIdx);

    List<Map<String, Object>> getPaymentListById(Integer idx);

    Payment getPaymentInfoById(Integer idx, int matchIdx);

    void updatePayment(int payment_idx);

    List<Payment> getPaymentListByMatchIdx(int match_idx);

    List<Integer> getPlayerCountByMonth();

    List<HashMap<String, Object>> userPaymentList(int id);

    int getPaymentCount(int id);

    List<Integer> getPlayerCountByMonth(Integer business_idx);

    List<Map<String, Object>> getUserPaymentListByMatchIdx(int match_idx);
}
