package com.Shoots.service;

import com.Shoots.domain.Faq;
import com.Shoots.mybatis.mapper.FaqMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqServiceImpl implements FaqService{

    private FaqMapper dao;
    public FaqServiceImpl(FaqMapper dao){
        this.dao = dao;
    }

    @Override
    public List<Faq> getFaqList() {
        return dao.getFaqList();
    }

    @Override
    public int getListCount() {
        return dao.getListCount();
    }

    @Override
    public Faq faqDetail(int id) {
        return dao.detailFaq(id);
    }

    @Override
    public Faq getDetailFaq(int id) {
        return dao.getDetailFaq(id);
    }

    @Override
    public void insertFaq(Faq faq) {
        dao.insertFaq(faq);
    }

    @Override
    public int updateFaq(Faq faq) {
        return dao.updateFaq(faq);
    }


    @Override
    public void deleteFaq(int id) {
        dao.deleteFaq(id);
    }
}
