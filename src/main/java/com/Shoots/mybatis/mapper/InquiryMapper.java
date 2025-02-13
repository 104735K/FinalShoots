package com.Shoots.mybatis.mapper;

import com.Shoots.domain.Inquiry;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface InquiryMapper {
    public int getListCount(String usertype, int idx);
    public List<Inquiry> getInquiryList(HashMap<String,Object> map);
    public void insertInquiry(Inquiry inquiry);
    public Inquiry getDetail(int inquiry_idx);
    public int inquiryModify(Inquiry inquiryData);
    public int inquiryDelete (int inquiry_idx);
    public List<Inquiry> getInquiryAdminList(HashMap<String, Object> map);
    public int getAdminListCount();
    public int replyComplete(int inquiry_idx);
    public List<Inquiry> getMyInquiryList(int id);
    public int getMyInquiryListCount(int id);
}
