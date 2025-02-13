package com.Shoots.service;

import com.Shoots.domain.Payment;
import com.Shoots.mybatis.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private PaymentMapper dao;

    public PaymentServiceImpl(PaymentMapper dao) {
        this.dao = dao;
    }

    @Override
    @Transactional
    public void insertPayment(Payment payment) {
        dao.insertPayment(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPaidForMatch(int idx, int match_idx) {
        return dao.hasPaidForMatch(idx, match_idx);
    }

    @Override
    @Transactional(readOnly = true)
    public int getPlayerCount(int match_idx) {
        return dao.getPlayerCount(match_idx);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPaymentListById(Integer idx) {
        return dao.getPaymentListById(idx);
    }

    @Override
    public Payment getPaymentInfoById(Integer idx, int match_idx) {
        return dao.getPaymentInfoById(idx, match_idx);
    }

    @Override
    public void updatePayment(int payment_idx) {
        dao.updatePayment(payment_idx);
    }

    @Override
    public List<Payment> getPaymentListByMatchIdx(int match_idx) {
        return dao.getPaymentListByMatchIdx(match_idx);
    }

    @Override
    public List<Integer> getPlayerCountByMonth() {
        return List.of();
    }

    @Override
    public List<Integer> getPlayerCountByMonth(Integer business_idx) {
        List<Map<String, Object>> results = dao.getPlayerCountByMonth(business_idx);
        List<Integer> monthlyData = new ArrayList<>(Collections.nCopies(12, 0));

        for (Map<String, Object> row : results) {
            Integer month = (Integer) row.get("month");
            Integer playerCount = ((Number) row.get("player_count")).intValue();
            monthlyData.set(month - 1, playerCount);
        }

        return monthlyData;
    }

    @Override
    public List<Map<String, Object>> getUserPaymentListByMatchIdx(int match_idx) {
        return dao.getUserPaymentListByMatchIdx(match_idx);
    }

    @Override
    public List<HashMap<String, Object>> userPaymentList(int id) {
        return dao.userPaymentList(id);
    }

    @Override
    public int getPaymentCount(int id) {
        return dao.getPaymentCount(id);
    }
}
