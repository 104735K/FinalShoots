package com.Shoots.controller;

import com.Shoots.domain.Inquiry;
import com.Shoots.domain.InquiryComment;
import com.Shoots.domain.PaginationResult;
import com.Shoots.service.InquiryCommentService;
import com.Shoots.service.InquiryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping(value = "/inquiry")
public class InquiryController {

    @Value("${my.savefolder}")
    private String saveFolder;

    private static final Logger logger = LoggerFactory.getLogger(InquiryController.class);
    private InquiryService inquiryService;
    private InquiryCommentService inquiryCommentService;

    public InquiryController(InquiryCommentService inquiryCommentService, InquiryService inquiryService) {
        this.inquiryCommentService = inquiryCommentService;
        this.inquiryService = inquiryService;
    }

    @GetMapping(value = "/list")
    public ModelAndView inquiryList(@RequestParam(defaultValue = "1") int page,
                                    ModelAndView mv, HttpSession session) {
        int idx = (int) session.getAttribute("idx");
        String usertype = (String) session.getAttribute("usertype");

        session.setAttribute("referer", "list");
        int limit = 10;

        int listcount = inquiryService.getListCount(usertype, idx);
        List<Inquiry> inquiryList = inquiryService.getInquiryList(page, limit, idx, usertype);

        for (Inquiry inquiry : inquiryList) {
            boolean replyExist = inquiryService.replyComplete(inquiry.getInquiry_idx());
            inquiry.setHasReply(replyExist);
        }

        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("inquiry/inquiryList");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("listcount", listcount);
        mv.addObject("inquiryList", inquiryList);
        mv.addObject("limit", limit);
        return mv;
    }


    @GetMapping(value = "/write")//board/write
    public String inquiryWrite() {
        return "inquiry/inquiryWrite";
    }

    @PostMapping("/add")
    public String add(Inquiry inquiry) throws Exception {
        MultipartFile uploadfile = inquiry.getUploadfile();

        if (!uploadfile.isEmpty()) {
            String fileDBName = inquiryService.saveUploadedFile(uploadfile, saveFolder);
            inquiry.setInquiry_file(fileDBName);    //바뀐 파일명으로 저장
            inquiry.setOriginal_file(uploadfile.getOriginalFilename());//원래 파일명 저장
        }

        inquiryService.insertInquiry(inquiry); //저장메소드 호출
        return "redirect:list";
    }

    @GetMapping("/detail")
    public ModelAndView detail(
            int inquiry_idx, ModelAndView mv, @RequestHeader(value = "referer", required = false) String beforeURL,
            HttpSession session) {
        /*
            1. String beforeURL = request.getHeader("referer"); 의미로
                어느 주소에서 detail로 이동했는지 header의 정보 중에서 "referer"를 통해 알 수 있습니다.
            2. 수정 후 이곳으로 이동하는 경우 조회수는 증가하지 않도록 합니다.
            3. myhome4/inquiryData/list에서 제목을 클릭한 경우 조회수가 증가하도록 합니다.
         */
        String sessionReferer = (String) session.getAttribute("referer");
        logger.info("referer: " + beforeURL);

        if(sessionReferer != null && sessionReferer.equals("list")){
//            if(beforeURL != null && beforeURL.endsWith("list")){ //조회수 올리기 위한 조건문
//                inquiryService.setReadCountUpdate(num);
//            }
            session.removeAttribute("referer");
        }

        //문의글의 내용과 (inquiryData), 해당 문의글에 달린 답변들 (icList)
        Inquiry inquiryData = inquiryService.getDetail(inquiry_idx);
        boolean replyExist = inquiryService.replyComplete(inquiryData.getInquiry_idx()); //답변유무에 따른 수정,삭제 버튼 나타냄/없앰
        inquiryData.setHasReply(replyExist);

        List<InquiryComment> icList = inquiryCommentService.getInquiryCommentList(inquiry_idx);

        //inquiryData=null; //error 페이지 이동 확인하고자 임의로 지정합니다.
        if(inquiryData == null){
            logger.info("상세보기 실패");
            mv.setViewName("403");
        }else{
            logger.info("상세보기 성공");
            int icListCount = inquiryCommentService.getListCount(inquiry_idx);
            mv.setViewName("inquiry/inquiryDetail");
            mv.addObject("icListCount", icListCount);
            mv.addObject("inquiryData", inquiryData);
            mv.addObject("icList", icList);
        }
        return mv;
    }


    @GetMapping("/modifyView")
    public ModelAndView inquiryModifyView(
            int inquiry_idx, ModelAndView mv, HttpServletRequest request ){

        Inquiry inquiryData = inquiryService.getDetail(inquiry_idx);

        //글 내용 불러오기 실패한 경우입니다.
        if(inquiryData == null){
            logger.info("수정보기 실패");
            mv.setViewName("403");
        }else{
            logger.info("(수정)상세보기 성공");
            //수정 홈 페이지로 이동할 때 원문 글 내용을 보여주기 때문에 inquiryData 객체를
            //ModelAndView 객체에 저장합니다.
            mv.addObject("inquiryData", inquiryData);
            //글 수정 폼 페이지로 이동하기 위해 경로를 설정합니다.
            mv.setViewName("inquiry/inquiryModify");
        }
        return mv;
    }


    @PostMapping("/modifyAction")
    public String inquiryModifyAction(
            Inquiry inquiryData, String check, RedirectAttributes rattr) throws Exception{

        logger.info("inquiry = " + inquiryData.getInquiry_idx());

        String url = "";
        MultipartFile uploadfile = inquiryData.getUploadfile();


        if(check != null && !check.equals("")){ //기본 파일 그대로 사용하는 경우
            inquiryData.setOriginal_file(check); //원래 넣어놓은 file은 modifyForm 에 input hidden으로 file값을 입력해놔서 db에 저장이 됨.
        }else{
            if(uploadfile != null && !uploadfile.isEmpty()){ //업로드 한 파일이 있을때.
                logger.info("파일 변경되었습니다.");
                String fileDBName = inquiryService.saveUploadedFile(uploadfile, saveFolder);
                inquiryData.setInquiry_file(fileDBName);    //바뀐 파일명으로 저장
                inquiryData.setOriginal_file(uploadfile.getOriginalFilename());//원래 파일명 저장
            }else{//기존에 파일이 없는데 파일 선택하지 않은 경우 + 기존 파일이 있었는데 삭제한 경우
                logger.info("선택 파일 없음.");
                inquiryData.setInquiry_file("");
                inquiryData.setOriginal_file("");
            }
        }

        //DAO에서 수정 메서드 호출하여 수정합니다.
        int result = inquiryService.inquiryModify(inquiryData);

        //수정에 실패한 경우
        if(result == 0){
            logger.info("게시판 수정 실패");
            url = "error/error";
        }else{//수정 성공의 경우
            logger.info("게시판 수정 완료");
            //수정한 글 내용을 보여주기 위해 글 내용 보기 보기 페이지로 이동하기 위해 경로를 설정합니다.
            url = "redirect:detail";
            rattr.addAttribute("inquiry_idx", inquiryData.getInquiry_idx());
        }
        return url;
    }


    @ResponseBody
    @PostMapping("/down")
    public byte[] BoardFileDown(String filename,String original, HttpServletResponse response) throws Exception{

        //수정
        String sFilePath = saveFolder + filename;

        File file = new File(sFilePath);

        //org.springframework.util.FileCopyUtils.copyToByteArray(File file) - File 객체를 읽어서 바이트 배열로 반환해주는 클래스
        byte[] bytes = FileCopyUtils.copyToByteArray(file);

        String sEncoding = new String(original.getBytes("UTF-8"), "iso-8859-1");
        //Content-Disposition : attachment : 브라우저는 해당 Content를 처리하지 않고, 다운로드하게 됩니다.
        response.setHeader("Content-Disposition", "attachment; filename=" + sEncoding);

        response.setContentLength(bytes.length);
        return bytes;
    }


    @PostMapping("/delete")
    public String inquiryDelete(int inquiry_idx, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int result = inquiryService.inquiryDelete(inquiry_idx);

        //삭제 처리 실패한 경우
        if(result == 0){
            logger.info("문의글 삭제 실패");
            return "error/404";
        } else {
            logger.info("문의글 삭제 성공");
            out.println("<script type='text/javascript'>");
            out.println("alert('성공적으로 삭제되었습니다.')");
            out.println("location.href='/Shoots/inquiry/list';");
            out.println("</script>");
        }
        return null;
    }


}
