package com.Shoots.controller;
import java.io.File;
import com.Shoots.domain.Notice;
import com.Shoots.domain.PaginationResult;
import com.Shoots.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value="/notice")
public class NoticeController {

    @Value("${my.savefolder}")
    private String saveFolder;

    private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);
    private NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping(value="/list")
    public ModelAndView NoticeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            ModelAndView mv,
            HttpSession session,
            @RequestParam(defaultValue = "") String search_word
    ){
        session.setAttribute("referer", "list");

        int listcount = noticeService.getSearchListCount(search_word); //총 리스트 수를 받아옴
        List<Notice> list = noticeService.getSearchList(search_word, page, limit); //리스트를 받아옴

        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("notice/noticeList");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("listcount", listcount);
        mv.addObject("noticelist", list);
        mv.addObject("limit", limit);
        mv.addObject("pagination", result);
        mv.addObject("search_word", search_word);

        return mv;
    }

    @GetMapping(value="/detail")
    public ModelAndView Detail(
            int id,
            ModelAndView mv,
            HttpServletRequest request,
            @RequestHeader(value="referer", required = false) String beforeURL, HttpSession session){

        String sessionReferer = (String) session.getAttribute("referer");
        logger.info("referer: " + beforeURL);
        if(sessionReferer != null && sessionReferer.equals("list")){
            if(beforeURL != null && beforeURL.contains("list")){
                noticeService.setReadCountUpdate(id);
            }
            session.removeAttribute("referer");
        }

        Notice notice = noticeService.getDetail(id);
        if(notice == null){
            logger.info("상세보기 실패");
            //mv.setViewName("error/error");
            //mv.addObject("url", request.getRequestURL());
            //mv.addObject("message", "상세보기 실패입니다.");
        } else{
            logger.info("상세보기 성공");

            mv.setViewName("notice/noticeDetail");
            mv.addObject("noticedata", notice);

        }
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

        byte[] bytes = FileCopyUtils.copyToByteArray(file);

        String sEncoding = new String(original.getBytes("utf-8"), "ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + sEncoding);

        response.setContentLength(bytes.length);
        return bytes;
    }

}
