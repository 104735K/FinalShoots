package com.Shoots.controller;

import java.io.File;

import com.Shoots.domain.*;
import com.Shoots.service.*;
import com.Shoots.service.ReportService;
import com.Shoots.task.SendMailText;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/testAdmin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


    @Value("${my.savefolder}")
    private String saveFolder;

    private FaqService faqService;
    private NoticeService noticeService;
    private PostService postService;
    private BusinessUserService businessUserService;
    private RegularUserService regularUserService;
    private InquiryService inquiryService;
    private SendMailText sendMailText;
    private ReportService reportService;
    private PostCommentService postCommentService;

    public AdminController(
            FaqService faqService,
            NoticeService noticeService,
            PostService postService,
            BusinessUserService businessUserService,
            RegularUserService regularUserService,
            InquiryService inquiryService,
            SendMailText sendMailText,
            PostCommentService postCommentService,
            ReportService reportService) {
        this.faqService = faqService;
        this.noticeService = noticeService;
        this.postService = postService;
        this.businessUserService = businessUserService;
        this.regularUserService = regularUserService;
        this.inquiryService = inquiryService;
        this.sendMailText = sendMailText;
        this.postCommentService = postCommentService;
        this.reportService = reportService;
    }

    @GetMapping
    public ModelAndView admin(ModelAndView mv, @RequestParam(defaultValue = "") String search_word){
        int faqcount = faqService.getListCount();   //faq 개수
        int noticecount = noticeService.getSearchListCount(search_word);    //공지사항 개수
        int inquirycount = inquiryService.getAdminListCount();  //문의사항 개수
        int postcount = postService.getAdminListCount(search_word);
        int businesscount = businessUserService.listApprovedCount(search_word);//연계 기업 수
        int usercount = regularUserService.listCount(search_word);//총 회원 수
        List<Map<String, Object>> userCount = regularUserService.getRegularUser(); //register_date에 따른 가입 회원수
        int allUsers = regularUserService.allUsers();
        List<Map<String, Object>> businessCount = businessUserService.getBusinessUsers(); //register_date에 따른 승인 기업 수
        List<Map<String, Object>> postCount = postService.getPostCount();   //reigser_date에 따른 게시글 수
        List<Map<String, Object>> categoryCount = postService.getCategoryCount();

        mv.addObject("faqcount", faqcount);
        mv.addObject("noticecount", noticecount);
        mv.addObject("inquirycount", inquirycount);
        mv.addObject("postcount", postcount);
        mv.addObject("businesscount", businesscount);
        mv.addObject("usercount", usercount);
        mv.addObject("userCount", userCount);
        mv.addObject("allUsers", allUsers);
        mv.addObject("businessCount", businessCount);
        mv.addObject("postCount", postCount);
        mv.addObject("categoryCount", categoryCount);
        mv.setViewName("admin/admin");
        return mv;
    }

    //faq 리스트
    @GetMapping(value="/faqList")
    public ModelAndView faqList(
            ModelAndView mv){
        int listcount = faqService.getListCount();
        List<Faq> list = faqService.getFaqList();

        mv.setViewName("admin/faqList");
        mv.addObject("list", list);
        mv.addObject("listcount", listcount);
        return mv;
    }

    //faq 상세보기
    @GetMapping(value="/faqDetail")
    public ModelAndView faqDetail(int id, ModelAndView mv, HttpServletRequest request){
        Faq faq = faqService.faqDetail(id);
        if(faq == null){
            logger.info("자세히 보기 실패");
        } else{
            mv.setViewName("admin/faqDetail");
            mv.addObject("faq", faq);
        }
        return mv;
    }

    //faq 글쓰기 페이지
    @GetMapping(value="/faqWrite")
    public String faqWrite(){
        return "admin/faqWrite";
    }

    //faq 추가
    @PostMapping("/faqAdd")
    public String faqAdd(Faq faq, HttpServletRequest request) throws Exception{

        MultipartFile uploadfile = faq.getUploadfile();
        if(!uploadfile.isEmpty()){
            String fileDBName = faqService.saveUploadFile(uploadfile, saveFolder);
            faq.setFaq_file(fileDBName);    //바뀐 파일명으로 저장
            faq.setFaq_original(uploadfile.getOriginalFilename());  //원래 파일명 저장
        }
        faqService.insertFaq(faq);

        return "redirect:faqList";
    }

    //faq 수정 폼
    @GetMapping(value="/faqUpdate")
    public ModelAndView faqUpdate(int id, HttpServletRequest request, ModelAndView mv) {
        Faq faq = faqService.getDetailFaq(id);
        if(faq == null){
            logger.info("수정 보기 실패");
        } else{
            logger.info("수정보기 성공");
            mv.setViewName("admin/faqUpdate");
            mv.addObject("faq", faq);
        }
        return mv;
    }

    //faq 수정
    @PostMapping(value="/faqUpdateProcess")
    public String faqUpdateProcess(Faq faq, Model model,String check,
                                   HttpServletRequest request, RedirectAttributes rattr) throws Exception{
        String url="";
        MultipartFile uploadfile = faq.getUploadfile();
        if(check != null && !check.isEmpty()){
            logger.info("기존파일 그대로 사용합니다.");
            faq.setFaq_original(check);
        } else{
            if(uploadfile != null && !uploadfile.isEmpty()){
                logger.info("파일이 변경되었습니다.");
                String fileDBName = faqService.saveUploadFile(uploadfile, saveFolder);
                faq.setFaq_file(fileDBName);
                faq.setFaq_original(uploadfile.getOriginalFilename());
            } else{
                logger.info("선택 파일이 없습니다.");
                faq.setFaq_file("");
            }
        }
        int result = faqService.updateFaq(faq);
        if(result == 1){
            rattr.addFlashAttribute("result", "updateSuccess");
            return "redirect:faqList";
        }else{
            //model.addAttribute("url", request.getRequestURL());
            return "redirect:faqList"; // error/error
        }
    }

    //faq 삭제
    @GetMapping(value="/faqDelete")
    public String faqDelete(int id){
        faqService.deleteFaq(id);
        return "redirect:faqList";
    }

    //notice list
    @GetMapping(value="/noticeList")
    public ModelAndView NoticeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            ModelAndView mv,
            @RequestParam(defaultValue = "") String search_word
    ){
        int listcount = noticeService.getSearchListCount(search_word); //총 리스트 수를 받아옴
        List<Notice> list = noticeService.getSearchList(search_word, page, limit); //리스트를 받아옴

        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("admin/noticeList");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("listcount", listcount);
        mv.addObject("noticelist", list);
        mv.addObject("limit", limit);
        mv.addObject("search_word", search_word);

        return mv;
    }

    //notice detail
    @GetMapping(value="/noticeDetail")
    public ModelAndView noticeDetail(
            int id,
            ModelAndView mv,
            HttpServletRequest request){
        Notice notice = noticeService.getDetail(id);
        if(notice == null){
            logger.info("상세보기 실패");
            //mv.setViewName("error/error");
            //mv.addObject("url", request.getRequestURL());
            //mv.addObject("message", "상세보기 실패입니다.");
        } else{
            logger.info("상세보기 성공");

            mv.setViewName("admin/noticeDetail");
            mv.addObject("noticedata", notice);

        }
        return mv;
    }

    //notice 글쓰기 페이지
    @GetMapping(value="/noticeWrite")
    public String noticeWrite(){
        return "admin/noticeWrite";
    }

    //notice 추가
    @PostMapping("/noticeAdd")
    public String noticeAdd(Notice notice, HttpServletRequest request) throws Exception{

        MultipartFile uploadfile = notice.getUploadfile();
        if(!uploadfile.isEmpty()){
            String fileDBName = faqService.saveUploadFile(uploadfile, saveFolder);
            notice.setNotice_file(fileDBName);    //바뀐 파일명으로 저장
            notice.setNotice_original(uploadfile.getOriginalFilename());  //원래 파일명 저장
        }
        noticeService.insertNotice(notice);

        return "redirect:noticeList";
    }
    //notice 수정 폼
    @GetMapping(value="/noticeUpdate")
    public ModelAndView noticeUpdate(int id, HttpServletRequest request, ModelAndView mv) {
        Notice notice = noticeService.getDetail(id);
        if(notice == null){
            logger.info("수정 보기 실패");
        } else{
            logger.info("수정보기 성공");
            mv.setViewName("admin/noticeUpdate");
            mv.addObject("notice", notice);
        }
        return mv;
    }

    //notice 수정
    @PostMapping(value="/noticeUpdateProcess")
    public String noticeUpdateProcess(Notice notice, String check, Model model, HttpServletRequest request,
                                      RedirectAttributes rattr) throws Exception{
        String url="";
        MultipartFile uploadfile = notice.getUploadfile();

        if(check != null && !check.isEmpty()){
            logger.info("기존파일 그대로 사용합니다.");
            notice.setNotice_original(check);
        } else{
            if(uploadfile != null && !uploadfile.isEmpty()){
                logger.info("파일 변경되었습니다.");
                String fileDBName = noticeService.saveUploadFile(uploadfile, saveFolder);
                notice.setNotice_file(fileDBName);
                notice.setNotice_original(uploadfile.getOriginalFilename());
            } else{
                logger.info("선택 파일 없습니다.");
                notice.setNotice_file("");
            }
        }

        int result = noticeService.updateNotice(notice);
        if(result == 1){
            rattr.addFlashAttribute("result", "updateSuccess");
            return "redirect:noticeList";
        }else{
            //model.addAttribute("url", request.getRequestURL());
            return "redirect:"; // error/error
        }
    }

    //notice 삭제
    @GetMapping(value="/noticeDelete")
    public String noticeDelete(int id){
        noticeService.deleteNotice(id);
        return "redirect:noticeList";
    }

    //다운
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
        //Content-Disposition: attachment: 브라우저는 해당 Content를 처리하지 않고, 다운로도하게 됩니다.
        response.setHeader("Content-Disposition", "attachment;filename=" + sEncoding);

        response.setContentLength(bytes.length);
        return bytes;
    }

    //post list
    @GetMapping(value="/postList")
    public ModelAndView PostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            ModelAndView mv,
            @RequestParam(defaultValue = "") String search_word
    ){
        int listcount = postService.getAdminListCount(search_word); //총 리스트 수를 받아옴
        List<Post> list = postService.getAdminPostList(search_word, page, limit); //리스트를 받아옴

        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("admin/postList");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("listcount", listcount);
        mv.addObject("postlist", list);
        mv.addObject("limit", limit);
        mv.addObject("search_word", search_word);

        return mv;
    }

    //post 삭제
    @GetMapping(value="/postDelete")
    public String postDelete(int num){
        postService.postDelete(num);
        return "redirect:postList";
    }

    //inquiry list
    @GetMapping(value="/inquiryList")
    public ModelAndView InquiryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            ModelAndView mv
    ){
        int listcount = inquiryService.getAdminListCount(); //총 리스트 수를 받아옴
        List<Inquiry> list = inquiryService.getInquiryAdminList(page, limit); //리스트를 받아옴

        for(Inquiry i : list){
            boolean replyExist = inquiryService.replyComplete(i.getInquiry_idx());
            i.setHasReply(replyExist);
        }
        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("admin/inquiryList");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("listcount", listcount);
        mv.addObject("inquirylist", list);
        mv.addObject("limit", limit);

        return mv;
    }

    //inquiry 삭제
    @GetMapping(value="/inquiryDelete")
    public String inquiryDelete(int inquiry_idx){
        inquiryService.inquiryDelete(inquiry_idx);
        return "redirect:inquiryList";
    }

    //business list
    @GetMapping(value="/businessList")
    public ModelAndView BusinessList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "") String search_word,
            ModelAndView mv
    ){
        int listcount = businessUserService.listCount(search_word); //총 리스트 수를 받아옴
        List<BusinessUser> list = businessUserService.getList(search_word, page, limit); //리스트를 받아옴

        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("admin/businessList");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("listcount", listcount);
        mv.addObject("businesslist", list);
        mv.addObject("limit", limit);
        mv.addObject("search_word", search_word);

        return mv;
    }

    //business approve
    @GetMapping(value="/businessApprove")
    public String businessApprove(int id) throws IOException {
        String email = businessUserService.getEmail(id);
        businessUserService.approveStatus(id);
        MailVO vo = new MailVO();
        vo.setTo(email);
        vo.setSubject("Shoots와 함께 해주셔서 감사합니다.");
        vo.setText("Shoots와 연계 기업이 되었음을 알려드립니다.");
        sendMailText.sendMail(vo);

        return "redirect:businessList";
    }

    //business refuse
    @GetMapping(value="/businessRefuse")
    public String businessRefuse(int id){
        businessUserService.refuseStatus(id);
        String email = businessUserService.getEmail(id);
        MailVO vo = new MailVO();
        vo.setTo(email);
        vo.setSubject("Shoots 연계 기업 안내");
        vo.setText("Shoots에 가입 신청이 거부 되었습니다.");
        sendMailText.sendMail(vo);
        return "redirect:businessList";
    }

    //business approved list
    @GetMapping(value="/businessApprovedList")
    public ModelAndView BusinessApprovedList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "") String search_word,
            ModelAndView mv
    ){
        int listcount = businessUserService.listApprovedCount(search_word);//총 리스트 수를 받아옴
        List<BusinessUser> list = businessUserService.getApprovedList(search_word, page, limit); //리스트를 받아옴

        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("admin/approvedBusinessList");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("listcount", listcount);
        mv.addObject("approvedbusinesslist", list);
        mv.addObject("limit", limit);
        mv.addObject("search_word", search_word);

        return mv;
    }

    //regular_user list
    @GetMapping(value="/userList")
    public ModelAndView UserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "") String search_word,
            ModelAndView mv
    ){
        int listcount = regularUserService.listCount(search_word);//총 리스트 수를 받아옴
        List<RegularUser> list = regularUserService.getUserList(search_word, page, limit); //리스트를 받아옴

        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("admin/userList");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("listcount", listcount);
        mv.addObject("userList", list);
        mv.addObject("limit", limit);
        mv.addObject("search_word", search_word);
        return mv;
    }

    //user common으로
    @GetMapping(value="/setUserCommon")
    public String setUserCommon(int id){
        regularUserService.setCommonUser(id);
        return "redirect:userList";
    }

    //user admin으로
    @GetMapping(value="/setUserAdmin")
    public String setUserAdmin(int id){
        regularUserService.setAdminUser(id);
        String email = regularUserService.getEmail(id);
        MailVO vo = new MailVO();
        vo.setTo(email);
        vo.setSubject("Shoots 권한 변경 안내");
        vo.setText("Shoots의 관리자로 권한이 변경되었음을 알려드립니다.");
        sendMailText.sendMail(vo);
        return "redirect:userList";
    }


    //report 리스트
    @GetMapping(value="/reportList")
    public ModelAndView reportList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            ModelAndView mv){
        int listcount = reportService.getReportCount();
        List<Map<String, Object>> list = reportService.getReportList(page, limit);
        PaginationResult result = new PaginationResult(page, limit, listcount);
        System.out.println(list);

        mv.setViewName("admin/reportList");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("list", list);
        mv.addObject("listcount", listcount);
        mv.addObject("limit", limit);

        return mv;
    }

    //report post block
    @GetMapping(value="/postBlock")
    public String postBlock(int id){
        postService.setBlock(id);
        return "redirect:reportList";
    }

    //report post_comment block
    @GetMapping(value="/commentBlock")
    public String commentBlock(int id){
        postCommentService.setBlock(id);
        return "redirect:reportList";
    }
}
