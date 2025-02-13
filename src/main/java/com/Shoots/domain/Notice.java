package com.Shoots.domain;

import org.springframework.web.multipart.MultipartFile;

public class Notice {
    private int notice_idx;
    private int writer;
    private String title;
    private String content;
    private String notice_file;
    private String notice_original;
    private String register_date;
    private int readcount;
    private String name;

    private MultipartFile uploadfile;

    public MultipartFile getUploadfile() {
        return uploadfile;
    }

    public void setUploadfile(MultipartFile uploadfile) {
        this.uploadfile = uploadfile;
    }

    public String getNotice_original() {
        return notice_original;
    }

    public void setNotice_original(String notice_original) {
        this.notice_original = notice_original;
    }

    public int getNotice_idx() {
        return notice_idx;
    }

    public void setNotice_idx(int notice_idx) {
        this.notice_idx = notice_idx;
    }

    public int getWriter() {
        return writer;
    }

    public void setWriter(int writer) {
        this.writer = writer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNotice_file() {
        return notice_file;
    }

    public void setNotice_file(String notice_file) {
        this.notice_file = notice_file;
    }

    public String getRegister_date() {
        return register_date;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date;
    }

    public int getReadcount() {
        return readcount;
    }

    public void setReadcount(int readcount) {
        this.readcount = readcount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
