package com.Shoots.controller;

import com.Shoots.domain.Payment;
import com.Shoots.service.IamportService;
import com.Shoots.service.PaymentHistoryService;
import com.Shoots.service.PaymentService;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

@Controller
@AllArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private PaymentService paymentService;
    private PaymentHistoryService paymentHistoryService;
    private IamportService iamportService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> paymentAdd(@RequestBody Payment payment) {

        Map<String, Object> response = new HashMap<>();
        logger.info("결제 정보 payment : " + payment.toString());

        try {

            String paymentDetails = iamportService.getPaymentDetails(payment.getImp_uid());

            if (paymentDetails != null && paymentDetails.contains("\"status\":\"paid\"")) {
                logger.info("이미 결제된 정보 : " + payment.getImp_uid());

                payment.setPayment_status("paid");
                insertPaymentHistoryWithoutTransaction(payment);
                paymentService.insertPayment(payment);

                response.put("success", true);
                response.put("message", "이미 결제된 상태");
                response.put("data", new JSONObject(paymentDetails));

                return ResponseEntity.ok(response);
            }

            if (paymentDetails != null && paymentDetails.contains("\"status\":\"failed\"")) {
                logger.info("결제 중 실패 : " + payment.getImp_uid());

                payment.setPayment_status("failed");
                insertPaymentHistoryWithoutTransaction(payment);

                response.put("success", false);
                response.put("message", "결제 중 실패");
                response.put("data", paymentDetails);

                return ResponseEntity.ok(response);
            }


            payment.setPayment_method("card");
            payment.setPayment_status("paid");

            insertPaymentHistoryWithoutTransaction(payment);
            paymentService.insertPayment(payment);

            logger.info("결제 성공 : buyer = " + payment.getBuyer_idx() + ", Merchant_uid = " + payment.getMerchant_uid());

            response.put("success", true);
            response.put("message", "결제 성공");
            response.put("data", payment);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            logger.error("결제 실패 : buyer = " + payment.getBuyer_idx() + ", Merchant_uid = " + payment.getMerchant_uid() + ", error: " + e.getMessage());

            response.put("success", false);
            response.put("message", "결제 처리 중 오류 발생");
            response.put("error", e.getMessage());

            payment.setPayment_status("fail");

            throw new RuntimeException("결제 처리 중 오류 발생", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void insertPaymentHistoryWithoutTransaction(Payment payment) {
        paymentHistoryService.insertHistory(payment);
    }

    @PostMapping("/checkPayment")
    public ResponseEntity<?> checkPayment(@RequestBody Map<String, String> request) throws Exception {
        String merchant_uid = request.get("merchant_uid");

        String paymentDetails = iamportService.getPaymentStatusByMerchantUid(merchant_uid);

        if (paymentDetails.equals("no_impUid")) {
            logger.info("결제 조회 실패 (no_impUid) : response 키가 없음. merchant_uid = " + merchant_uid);
            return ResponseEntity.status(204).build();
        } else {
            try {
                JSONObject jsonResponse = new JSONObject(paymentDetails);

                if (jsonResponse.has("response")) {
                    JSONObject response = jsonResponse.getJSONObject("response");
                    String paymentStatus = response.getString("status");

                    logger.info("결제 조회 성공 checkPayment : payment details = " + paymentDetails);
                    if ("paid".equals(paymentStatus)) {
                        logger.info("결제 조회 (paid) 성공 : merchant_uid = " + merchant_uid + " 결제 상태 : paid");
                        return ResponseEntity.ok(response.toString());
                    } else if ("cancelled".equals(paymentStatus)) {
                        logger.info("결제 조회 (cancelled) 성공 : merchant_uid = " + merchant_uid + " 결제 상태 : cancelled");
                        return ResponseEntity.ok(response.toString());
                    } else if ("failed".equals(paymentStatus)) {
                        logger.info("결제 조회 : merchant_uid = " + merchant_uid + " 결제 상태 : failed");
                        return ResponseEntity.ok(response.toString());
                    } else {
                        logger.error("결제 조회 실패 : merchant_uid = " + merchant_uid + " 결제 상태 : " + paymentDetails);
                        return ResponseEntity.status(500).body(response.toString());
                    }
                } else {
                    logger.error("결제 조회 실패 : response 키가 없음. merchant_uid = " + merchant_uid);
                    return ResponseEntity.status(204).body("imp_uid 없음: 결제 정보를 찾지 못했습니다.");
                }
            } catch (JSONException e) {
                logger.error("JSON 파싱 오류: " + e.getMessage(), e);
                return ResponseEntity.status(400).body("JSON parsing error");
            }
        }
    }
}
