package com.Shoots.service;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface SmsService {
    ResponseEntity<String> sendSms();

    ResponseEntity<String> sendSms(String to);

    //MultipleDetailMessageSentResponse sendSmsMany(ArrayList<Message> messageList, boolean b, boolean b1);

    ResponseEntity<String> sendMany(List<Map<String, Object>> userList);
}
