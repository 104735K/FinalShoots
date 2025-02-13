package com.Shoots.domain;

import org.springframework.web.multipart.MultipartFile;

public class Post {
    private int post_idx;
    private int writer;
    private String category;
    private String title;
    private String content;
    private String post_file; //실제 저장된 파일의 이름
    private MultipartFile uploadfile;
    private Integer price;
    private String register_date;
    private int readcount;
    private String user_id;
    private int idx;
    private int comment_idx;
    private int comment_ref_id;
    private int commentCount;  // 댓글 수 추가
    private String user_file;
    private String post_original; //첨부될 파일의 이름
    private String status;
    private String report_status;

    public String getReport_status() {
        return report_status;
    }

    public void setReport_status(String report_status) {
        this.report_status = report_status;
    }

    private String is_secret;

    public String getIs_secret() {
        return is_secret;
    }

    public void setIs_secret(String is_secret) {
        this.is_secret = is_secret;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPost_original() {
        return post_original;
    }

    public void setPost_original(String post_original) {
        this.post_original = post_original;
    }




    public int getPost_idx() {
        return post_idx;
    }

    public void setPost_idx(int post_idx) {
        this.post_idx = post_idx;
    }

    public int getWriter() {
        return writer;
    }

    public void setWriter(int writer) {
        this.writer = writer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getPost_file() {
        return post_file;
    }

    public void setPost_file(String post_file) {
        this.post_file = post_file;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getComment_idx() {
        return comment_idx;
    }

    public void setComment_idx(int comment_idx) {
        this.comment_idx = comment_idx;
    }

    public int getComment_ref_id() {
        return comment_ref_id;
    }

    public void setComment_ref_id(int comment_ref_id) {
        this.comment_ref_id = comment_ref_id;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getUser_file() {
        return user_file;
    }

    public void setUser_file(String user_file) {
        this.user_file = user_file;
    }


    public MultipartFile getUploadfile() {
        return uploadfile;
    }

    public void setUploadfile(MultipartFile uploadfile) {
        this.uploadfile = uploadfile;
    }


}
