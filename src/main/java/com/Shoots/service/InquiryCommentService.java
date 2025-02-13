package com.Shoots.service;

import com.Shoots.domain.InquiryComment;

import java.util.List;

public interface InquiryCommentService {
    public int getListCount(int inquiry_idx);
    public List<InquiryComment> getInquiryCommentList(int inquiry_idx);
    public void insertInquiryComment(InquiryComment ic);
    public int inquiryCommentModify (InquiryComment ic);
    public int inquiryCommentDelete (int i_comment_idx);

}
