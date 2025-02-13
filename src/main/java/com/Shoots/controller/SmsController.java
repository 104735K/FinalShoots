package com.Shoots.controller;


import com.Shoots.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @GetMapping("/send-sms/{to}")
    public ResponseEntity<String> sendSms(
            @PathVariable("to") String to
    ){
        ResponseEntity<String> response = smsService.sendSms(to);
        return response;
    }


    @PostMapping("/send-many")
    public ResponseEntity<?> sendSmsMany(@RequestBody List<Map<String, Object>> userList) {
        return smsService.sendMany(userList);
    }


}
