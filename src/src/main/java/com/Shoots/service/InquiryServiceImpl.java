package com.Shoots.service;

import com.Shoots.domain.Inquiry;
import com.Shoots.mybatis.mapper.InquiryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class InquiryServiceImpl implements InquiryService {
    private static final Logger log = LoggerFactory.getLogger(InquiryServiceImpl.class);
    private InquiryMapper inquiryMapper;

    public InquiryServiceImpl(InquiryMapper inquiryMapper) {
        this.inquiryMapper = inquiryMapper;
    }

    @Override
    public int getListCount(String usertype, int idx) {
        return inquiryMapper.getListCount(usertype, idx);
    }

    @Override
    public List<Inquiry> getInquiryList(int page, int limit, int idx, String usertype) {
        HashMap<String, Object> map = new HashMap<>();
        int offset = (page - 1) * limit; // offset 페이지까지 생략 (offset 1 -> 2페이지부터 리스트뽑음)

        map.put("offset", offset);
        map.put("limit", limit);
        map.put("usertype", usertype);
        map.put("idx", idx);

        return inquiryMapper.getInquiryList(map);
    }

    @Override
    public void insertInquiry(Inquiry inquiry) {
        inquiryMapper.insertInquiry(inquiry);
    }


    @Override
    public Inquiry getDetail(int inquiry_idx) {
        return inquiryMapper.getDetail(inquiry_idx);
    }

    @Override
    public int inquiryModify(Inquiry inquiryData) {
        return inquiryMapper.inquiryModify(inquiryData);
    }

    @Override
    public int inquiryDelete(int inquiry_idx) {return inquiryMapper.inquiryDelete(inquiry_idx);}

    @Override
    public List<Inquiry> getInquiryAdminList(int page, int limit) {
        HashMap<String, Object> map = new HashMap<>();
        int offset = (page - 1) * limit;
        map.put("offset", offset);
        int pageSize = limit;
        map.put("pageSize", pageSize);
        return inquiryMapper.getInquiryAdminList(map);
    }

    @Override
    public int getAdminListCount() {
        return inquiryMapper.getAdminListCount();
    }

    @Override
    public boolean replyComplete(int inquiry_idx) {
        int result = inquiryMapper.replyComplete(inquiry_idx);
        return  result == 1;
    }

    @Override
    public List<Inquiry> getMyInquiryList(int id) {
        return inquiryMapper.getMyInquiryList(id);
    }

    @Override
    public int getMyInquiryListCount(int id) {
        return inquiryMapper.getMyInquiryListCount(id);
    }


}
