package com.Shoots.controller;

import com.Shoots.domain.Payment;
import com.Shoots.service.IamportService;
import com.Shoots.service.PaymentHistoryService;
import com.Shoots.service.PaymentService;
import lombok.AllArgsConstructor;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

@Controller
@RequestMapping("/refund")
@AllArgsConstructor
public class RefundController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentHistoryService paymentHistoryService;
    private PaymentService paymentService;
    private final IamportService iamportService;

    @Transactional
    @PostMapping("/refundProcess")
    public ResponseEntity<?> refundProcess(@RequestBody Payment payment) {

        Map<String, Object> mresponse = new HashMap<>();

        try {
            String paymentDetails = iamportService.getPaymentDetails(payment.getImp_uid());

            if (paymentDetails != null && paymentDetails.contains("\"status\":\"cancelled\"")) {
                logger.info("이미 환불된 정보 : " + payment.getImp_uid());

                payment.setPayment_status("refunded");
                paymentHistoryService.insertHistory(payment);
                paymentService.updatePayment(payment.getPayment_idx());

                mresponse.put("success", true);
                mresponse.put("message", "이미 환불된 상태");
                mresponse.put("data", paymentDetails);

                logger.info("DB삭제 및 결제이력 수정 완료");

                return ResponseEntity.ok(mresponse);
            }

            String accessToken = iamportService.getAccessToken();

            logger.info("엑세스 토큰 : " + accessToken);

            if (accessToken == null || accessToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("엑세스 토큰 발급 실패");
            }
                String jsonRefundRequest = "{"
                        + "\"imp_uid\": \"" + payment.getImp_uid() + "\","
                        + "\"merchant_uid\": \"" + payment.getMerchant_uid() + "\","
                        + "\"refund_amount\": " + payment.getPayment_amount()
                        + "}";

                HttpRequest requestRefund = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.iamport.kr/payments/cancel"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + accessToken)
                        .method("POST", HttpRequest.BodyPublishers.ofString(jsonRefundRequest, StandardCharsets.UTF_8))
                        .build();

            HttpResponse<String> responseRefund = HttpClient.newHttpClient().send(requestRefund, HttpResponse.BodyHandlers.ofString());

                if (responseRefund.statusCode() == 200) {
                    logger.info("환불 성공");
                    paymentService.updatePayment(payment.getPayment_idx());

                    payment.setPayment_status("refunded");
                    paymentHistoryService.insertHistory(payment);

                    JSONObject response = new JSONObject();
                    response.put("success", true);
                    response.put("message", "Refund success");

                    return ResponseEntity.ok(response.toString());
                } else {
                    logger.error("환불 실패 : 상태 코드 = {}, 메시지 = {}", responseRefund.statusCode(), responseRefund.body());
                    throw new RuntimeException("환불 처리 중 오류가 발생했습니다. 상태 코드: " + responseRefund.statusCode());
                }

            } catch (Exception e) {
            logger.error("환불 처리 중 오류 발생: ", e);
            throw new RuntimeException("환불 처리 중 오류 발생", e);        }
    }
}
