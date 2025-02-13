package com.Shoots;

import com.Shoots.domain.Match;
import com.Shoots.domain.Payment;
import com.Shoots.service.MatchService;
import com.Shoots.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class RefundScheduler {

    private final MatchService matchService;
    private final PaymentService paymentService;
    private final RestTemplate restTemplate;

    @Scheduled(cron = "0 0/30 9-23 * * ?")
    private void refundMatches(){

        log.info("=== 자동 환불 체크 시작 ===");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusHours = now.plusHours(2);

        LocalDate matchDate = nowPlusHours.toLocalDate();
        LocalTime matchTime = nowPlusHours.toLocalTime().withSecond(0).withNano(0);

        System.out.println("Match Date: " + matchDate);
        System.out.println("Match Time: " + matchTime);

        List<Match> matchList = matchService.getMatchListByMatchTime(matchDate, matchTime);

        System.out.println("Match List: " + matchList.toString());

        for (Match match : matchList) {
            int playerCount = paymentService.getPlayerCount(match.getMatch_idx());
            int playerMin = match.getPlayer_min();

            log.info("매치 IDX : {}, 현재 신청 인원 : {}, 최소 필요 인원 : {}", match.getMatch_idx(), playerCount, playerMin);


            if (playerCount < playerMin) {
                List<Payment> paymentList = paymentService.getPaymentListByMatchIdx(match.getMatch_idx());
                List<Map<String, Object>> userList = paymentService.getUserPaymentListByMatchIdx(match.getMatch_idx());

                for (Payment payment : paymentList) {
                    processRefund(payment);
                }
                for (Map<String, Object> user : userList) {
                    sendRefundNotification(user, "cancel");
                }
            }

            if (playerCount >= playerMin) {
                List<Map<String, Object>> userList = paymentService.getUserPaymentListByMatchIdx(match.getMatch_idx());

                for (Map<String, Object> user : userList) {
                    sendRefundNotification(user, "confirm");
                }
            }
        }

        log.info("=== 자동 환불 체크 종료 ===");
    }

    private void processRefund(Payment payment) {
        System.out.println("=============================================================");
        log.info("자동 환불 진행 : 결제 ID {}", payment.getPayment_idx());

        String refundApiUrl = "https://www.goshoots.site/Shoots/refund/refundProcess";
        // String refundApiUrl = "http://3.36.152.102:1111/refund/refundProcess";

        try {
            restTemplate.postForEntity(refundApiUrl, payment, String.class);
            log.info("자동 환불 성공 : 결제 IDX {}", payment.getPayment_idx());
        } catch (Exception e) {
            log.error("자동 환불 실패 : 결제 IDX {}, 오류: {}", payment.getPayment_idx(), e.getMessage());
        }
    }

    private void sendRefundNotification(Map<String, Object> user, String messageType) {
        System.out.println("=============================================================");
        log.info("매치 취소 및 확정 SMS 문자 전송");

        String phoneNumber = (String) user.get("tel");
        log.info("phoneNumber =  {}", phoneNumber);

        String smsApiUrl = "https://www.goshoots.site/Shoots/test/send-many";

        user.put("messageType", messageType);

        List<Map<String, Object>> userList = new ArrayList<>();
        userList.add(user);

        try {
            restTemplate.postForEntity(smsApiUrl, userList, String.class);
            log.info("SMS 전송 성공");
        } catch (Exception e) {
            log.error("SMS 전송 실패 : 오류 = {}", e.getMessage());
        }
    }

}
