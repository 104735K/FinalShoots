package com.Shoots.controller;

import com.Shoots.domain.Faq;
import com.Shoots.service.FaqService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping(value="/faq")
public class FaqController {
    @Value("${my.savefolder}")
    private String saveFolder;

    private FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping("/faqList")
    public ModelAndView faqList(ModelAndView mv) {
        List<Faq> list = faqService.getFaqList();
        int listcount = faqService.getListCount();

        mv.setViewName("faq/faqList");
        mv.addObject("faqList", list);
        mv.addObject("listcount", listcount);
        return mv;
    }

    @ResponseBody
    @PostMapping("/down")
    public byte[] BoardFileDown(String filename,
                                HttpServletRequest request,
                                String original,
                                HttpServletResponse response
    ) throws Exception{

        //수정
        String sFilePath = saveFolder + filename;

        File file = new File(sFilePath);

        //org.springframework.util.FileCopyUtils.copyToByteArray(File file) - File 객체를 읽어서 바이트 배열로 반환합니다.
        byte[] bytes = FileCopyUtils.copyToByteArray(file);

        String sEncoding = new String(original.getBytes("utf-8"), "ISO-8859-1");
        //Content-Disposition: attachment: 브라우저는 해당 Content를 처리하지 않고, 다운로도하게 됩니다.
        response.setHeader("Content-Disposition", "attachment;filename=" + sEncoding);

        response.setContentLength(bytes.length);
        return bytes;
    }
}
