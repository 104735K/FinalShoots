package com.Shoots.service;


import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import provider.SmsProvider;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final SmsProvider smsProvider;

    @Override
    public ResponseEntity<String> sendSms(String to) {

        try{
            boolean result = smsProvider.sendSms(to);
            if(!result) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메세지 전송에 실패했습니다.");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메세지 전송중 예외가 발생했습니다.");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메세지 전송에 성공했습니다.");
    }


    @Override
    public ResponseEntity<String> sendSms() {
        return ResponseEntity.badRequest().body("메세지 수신자가 없습니다.");
    }


    @Override
    public ResponseEntity<String> sendMany(List<Map<String, Object>> userList) {

        try {
            smsProvider.sendMany(userList);
            return ResponseEntity.ok("메시지 전송에 성공했습니다.");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("메시지 전송 중 예외가 발생했습니다.");
        }

    }
}
