package com.Shoots.domain;


public class MailVO {
    private String from = "chldudtn0206@naver.com"; //발신사
    private String to; //수신자
    private String subject="회원가입 축하함. - 제목"; //제목
    private String text ="회원가입 축하함. - 내용"; //내용

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
