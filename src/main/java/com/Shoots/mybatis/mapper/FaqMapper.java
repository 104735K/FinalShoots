package com.Shoots.mybatis.mapper;

import com.Shoots.domain.Faq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaqMapper {
    public List<Faq> getFaqList(); //faqList 가져오기

    public int getListCount(); //faqList 갯수 확인

    public Faq detailFaq(int id); //faq 상세 내용 확인

    public Faq getDetailFaq(int id);

    public void insertFaq(Faq faq); //faq 글 등록하기

    public int updateFaq(Faq faq); //faq 수정

    public void deleteFaq(int id); //faq 삭제

}
