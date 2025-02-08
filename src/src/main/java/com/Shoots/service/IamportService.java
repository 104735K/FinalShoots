package com.Shoots.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;


@Service
public class IamportService {

    private static final Logger log = LoggerFactory.getLogger(IamportService.class);
    @Value("${iamport.api.imp_key}")
    private String imp_key;

    @Value("${iamport.api.imp_secret}")
    private String imp_secret;
    private static final String IAMPORT_TOKEN_URL = "https://api.iamport.kr/users/getToken";
    private static final String IAMPORT_PAYMENT_URL = "https://api.iamport.kr/payments/";
    private static final String IAMPORT_FIND_PAYMENT_URL = "https://api.iamport.kr/payments/find/";

    public String getAccessToken() throws Exception {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "imp_key", imp_key,
                    "imp_secret", imp_secret
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(IAMPORT_TOKEN_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // 응답 본문을 파싱하여 access_token만 추출
                String accessToken = objectMapper.readTree(response.body())
                        .path("response")
                        .path("access_token")
                        .asText();

                System.out.println("=============================================================");
                System.out.println("엑세스 토큰 발급 성공 : " + accessToken);
                System.out.println("=============================================================");

                return accessToken;
            } else {
                System.out.println("=============================================================");
                System.out.println("엑세스 토큰 발급 실패 : " + response.body());
                System.out.println("=============================================================");

                return null;
            }
        } catch (Exception e) {
            System.out.println(" try-catch 엑세스 토근 발급 실패 : " + e.getMessage());
            return null;
        }
    }

    public String getPaymentStatusByMerchantUid(String merchant_uid) throws Exception {
        try {
            String url = IAMPORT_FIND_PAYMENT_URL + merchant_uid;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getAccessToken())
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("결제 (단건) 상태 조회 response : " + response.body());

            String impUid = extractImpUidFromResponse(response.body());

            if (impUid != null) {
                return getPaymentDetails(impUid);
            } else {
                System.out.println("imp_uid를 찾을 수 없음");
                return "no_impUid";
            }
        } catch (Exception e) {
            System.out.println("결제 (단건) 상태 조회 실패");
            e.printStackTrace();
            return null;
        }
    }

    private String extractImpUidFromResponse(String responseBody) {
        try {

            JSONObject jsonResponse = new JSONObject(responseBody);
            if (jsonResponse.getInt("code") == 0) {

                if (jsonResponse.has("response") && !jsonResponse.isNull("response")) {
                    JSONObject response = jsonResponse.getJSONObject("response");
                    return response.getString("imp_uid");
                } else {
                    System.out.println("response 객체가 존재하지 않음 또는 null");
                    return null;
                }
            } else {
                System.out.println("결제 (단건 - impUid 추출)상태 조회 실패 : " + jsonResponse.getString("message"));
                return null;
            }
        } catch (Exception e) {
            System.out.println("imp_uid 추출 중 오류 발생");
            e.printStackTrace();
            return null;
        }
    }


    public String getPaymentDetails(String imp_uid) throws Exception {
       System.out.println("imp_uid : " + imp_uid);
        try {
            String url = IAMPORT_PAYMENT_URL + imp_uid;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("결제 상태 조회 성공");

            return response.body();
        } catch (Exception e) {
            System.out.println("결제 상태 조회 실패");
            e.printStackTrace();
            return null;
        }
   }
}
