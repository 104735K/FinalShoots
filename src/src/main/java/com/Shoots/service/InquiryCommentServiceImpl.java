package com.Shoots.service;

import com.Shoots.domain.InquiryComment;
import com.Shoots.mybatis.mapper.InquiryCommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class InquiryCommentServiceImpl implements InquiryCommentService{
    private static final Logger logger = LoggerFactory.getLogger(InquiryCommentServiceImpl.class);
    private InquiryCommentMapper inquiryCommentMapper;

    public InquiryCommentServiceImpl(InquiryCommentMapper inquiryCommentMapper) {
        this.inquiryCommentMapper = inquiryCommentMapper;
    }

    @Override
    public int getListCount(int inquiry_idx) {
        return inquiryCommentMapper.getListCount(inquiry_idx);
    }


    @Override
    public List<InquiryComment> getInquiryCommentList(int inquiry_idx) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("inquiry_idx", inquiry_idx);
        return inquiryCommentMapper.getInquiryCommentList(map);
    }


    @Override
    public void insertInquiryComment(InquiryComment ic) {
        inquiryCommentMapper.insertInquiryComment(ic);
    }

    @Override
    public int inquiryCommentModify(InquiryComment ic) {
        return inquiryCommentMapper.inquiryCommentModify(ic);
    }

    @Override
    public int inquiryCommentDelete(int i_comment_idx) {
        return inquiryCommentMapper.inquiryCommentDelete(i_comment_idx);
    }
}
