package com.Shoots.task;

import com.Shoots.domain.MailVO;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SendMailText {
    private JavaMailSender mailSender;


    public SendMailText(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private static final Logger logger = LoggerFactory.getLogger(SendMailText.class);

    public void sendMail(MailVO vo) {
        MimeMessagePreparator mp = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                helper.setFrom(vo.getFrom()); // 이메일 발신자 설정
                helper.setTo(vo.getTo()); // 이메일 수신자 설정
                helper.setSubject(vo.getSubject()); // 이메일 제목 설정
                helper.setText(vo.getText(), false); // 텍스트 내용 설정, HTML 비활성화
            }
        };

        mailSender.send(mp); // 메일 전송
        logger.info("텍스트 메일 전송 완료.");
    }
}