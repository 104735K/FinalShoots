package com.Shoots.controller;

import com.Shoots.domain.*;
import com.Shoots.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "/myPage")
public class MyPageController {
    private static final Logger logger = LoggerFactory.getLogger(MyPageController.class);
    private final PostService postService;
    private final InquiryService inquiryService;
    private final PaymentService paymentService;
    private RegularUserService regularUserService;
    private PostCommentService postCommentService;

    public MyPageController(RegularUserService regularUserService, PostService postService, InquiryService inquiryService, PostCommentService postCommentService, PaymentService paymentService) {
        this.regularUserService = regularUserService;
        this.postService = postService;
        this.inquiryService = inquiryService;
        this.postCommentService = postCommentService;
        this.paymentService = paymentService;
    }

    @GetMapping(value = "/info")
    public ModelAndView info(ModelAndView mv, HttpServletRequest request, HttpSession session) {
        String id = (String) request.getSession().getAttribute("id");
        RegularUser user = regularUserService.regularUserList(id);

        if (user != null) {
            logger.info("상세보기 성공");
            mv.setViewName("myPage/info");
            mv.addObject("user", user);
        } else {
            logger.info("상세보기 실패");
        }

        return mv;
    }

    @ResponseBody
    @GetMapping(value = "/idcheck")
    public int idcheck(String id, HttpServletRequest request) {
        String my = (String) request.getSession().getAttribute("id");
        if (regularUserService.selectById(id) == -1 || id.equals(my)) {
            System.out.println("반환값: " + regularUserService.selectById(id));
            return -1;
        } else return 1;
    }

    @ResponseBody
    @GetMapping(value = "/emailcheck")
    public int emailcheck(String email, HttpServletRequest request) {
        int idx = (Integer) request.getSession().getAttribute("idx");
        String my = regularUserService.getEmail(idx);
        if (regularUserService.selectByEmail(email) == -1 || email.equals(my)) {
            System.out.println(regularUserService.selectByEmail(email));
            return -1;
        } else return 1;
    }


    @GetMapping(value = "/updateUser")
    public ModelAndView updateUser(ModelAndView mv, HttpServletRequest request) {
        String id = (String) request.getSession().getAttribute("id");
        RegularUser user = regularUserService.regularUserList(id);
        if (user != null) {
            logger.info("수정보기 성공");
            mv.setViewName("myPage/updateUser");
            mv.addObject("user", user);
        }

        return mv;
    }

    @PostMapping(value = "/updateUserProcess")
    public String updateUserProcess(RegularUser user, HttpServletRequest request, Model model, RedirectAttributes rattr) {
        regularUserService.updateRegularUser(user);
        rattr.addFlashAttribute("result", "updateSuccess");
        return "redirect:info";
    }

    @GetMapping(value = "/myPostList")
    public ModelAndView myPostList(ModelAndView mv, HttpServletRequest request) {
        Integer id = (Integer) request.getSession().getAttribute("idx");
        List<Post> list = postService.getMyPostList(id);
        int count = postService.getMyPostListCount(id);

        mv.setViewName("myPage/myPostList");
        mv.addObject("list", list);
        mv.addObject("count", count);

        return mv;
    }

    @GetMapping(value = "/myCommentList")
    public ModelAndView myCommentList(ModelAndView mv, HttpServletRequest request) {
        Integer id = (Integer) request.getSession().getAttribute("idx");
        List<PostComment> list = postCommentService.getMyCommentList(id);
        int count = postCommentService.getMyCommentListCount(id);

        mv.setViewName("myPage/myCommentList");
        mv.addObject("list", list);
        mv.addObject("count", count);

        return mv;
    }

    @GetMapping(value = "/myInquiryList")
    public ModelAndView myInquiryList(ModelAndView mv, HttpServletRequest request) {
        Integer id = (Integer) request.getSession().getAttribute("idx");
        int count = inquiryService.getMyInquiryListCount(id);
        List<Inquiry> list = inquiryService.getMyInquiryList(id);

        for (Inquiry i : list) {
            boolean replyExist = inquiryService.replyComplete(i.getInquiry_idx());
            i.setHasReply(replyExist);
        }

        mv.setViewName("myPage/myInquiryList");
        mv.addObject("list", list);
        mv.addObject("count", count);

        return mv;
    }

    @GetMapping(value = "/myMatchList")
    public ModelAndView myMatchList(ModelAndView mv, HttpServletRequest request) {
        Integer id = (Integer) request.getSession().getAttribute("idx");
        List<HashMap<String, Object>> list = paymentService.userPaymentList(id);
        int count = paymentService.getPaymentCount(id);

        System.out.println(list);
        mv.setViewName("myPage/myMatchList");
        mv.addObject("list", list);
        mv.addObject("count", count);

        return mv;
    }

    @PostMapping(value = "/deleteRegularUser")
    public String deleteRegularUser(@RequestParam int idx, RedirectAttributes redirectAttributes, HttpSession session) {
        int result = regularUserService.deleteRegularUser(idx);
        session.removeAttribute("idx");
        session.invalidate();

        if (result == 0) {
            logger.info("회원탈퇴 실패");
            return "error/404";
        } else {
            logger.info("회원탈퇴 됨.");
            redirectAttributes.addFlashAttribute("message", "경기장에서 다시 만날 날을 기다릴게요!");
            return "redirect:/main";
        }
    }

}
