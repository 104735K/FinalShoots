package com.Shoots.controller;

import com.Shoots.domain.InquiryComment;
import com.Shoots.service.InquiryCommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping(value = "/inquiryComment")
public class InquiryCommentController {

    private final static Logger logger = LoggerFactory.getLogger(InquiryCommentController.class);
    private InquiryCommentService inquiryCommentService;

    public InquiryCommentController(InquiryCommentService inquiryCommentService) {
        this.inquiryCommentService = inquiryCommentService;
    }


    //댓글 (문의답변)의 List 처리는 inquiryController 의 detail 에서 한번에 처리한 뒤 보내버림.


    @PostMapping(value = "/add")
    public String inquiryCommentAdd(InquiryComment ic, int inquiry_idx) {
        inquiryCommentService.insertInquiryComment(ic);
        logger.info("댓글 달린 문의글 (inquiry_idx)은 : " + inquiry_idx);
        return "redirect:/inquiry/detail?inquiry_idx=" + inquiry_idx;
    }

    @PostMapping(value = "/inquiryCommentModify")
    public ResponseEntity<String> inquiryCommentModify(InquiryComment ic) {

        int result = inquiryCommentService.inquiryCommentModify(ic);

        //수정에 실패한 경우
        if (result == 0) {
            logger.info("문의댓글 수정 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
        } else {
            logger.info("문의댓글 수정 완료");
            return ResponseEntity.ok("수정 성공");
        }
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public int inquiryCommentDelete(int i_comment_idx) {
        int result = inquiryCommentService.inquiryCommentDelete(i_comment_idx);
        return result;
    }

}
