package com.Shoots.controller;

import com.Shoots.domain.PaginationResult;
import com.Shoots.domain.Post;
import com.Shoots.service.PostCommentService;
import com.Shoots.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RestController
//@CrossOrigin(origins = "http://localhost:1000")
@Controller
@RequestMapping(value="/post")
public class PostController {

    @Value("${my.savefolder}")
    private String SAVE_FOLDER;

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private PostService postService;
    private PostCommentService postCommentService;

    //클래스에 생성자가 하나만 존재하는 경우 Spring이 자동으로 의존성을 주입해 주므로 @Autowired를 붙일 필요가 없습니다.
    //Spring Boot 2.6 이상에서는 생성자가 하나뿐인 경우 @Autowired를 생략하는 것을 권장합니다.
    //생성자 주입
    public PostController(PostService postService, PostCommentService postCommentService) {
        this.postService = postService;
        this.postCommentService = postCommentService;
    }



    @GetMapping(value = "/list")
    public ModelAndView postlist(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(defaultValue = "A") String category, // 기본 카테고리 추가
            @RequestParam(defaultValue = "available") String status, // 기본값 추가
            @RequestParam(defaultValue = "") String search_word,
            ModelAndView mv,
            HttpSession session) {

        session.setAttribute("referer", "list");

        int limit = 10; // 한 화면에 출력할 로우 갯수

//        // 페이지와 limit 값이 제대로 설정되었는지 확인
//        int start = Math.max(0, (page - 1) * limit); // start가 음수일 수 없도록 Math.max 사용
//        int end = limit; // 끝 값은 limit과 동일

        int listcount = postService.getListCount(category, search_word); // 총 리스트 수를 받아옴
        List<Post> list = postService.getPostList(page, limit, category, search_word, search_word); // 리스트를 받아옴

        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("post/post_list");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("listcount", listcount);
        mv.addObject("postlist", list);
        mv.addObject("limit", limit);
        mv.addObject("pagination", result); // PaginationResult 객체를 전달
        mv.addObject("category", category);
        mv.addObject("status", status);
        mv.addObject("search_word", search_word);
        return mv;
    }

    // AJAX 요청을 처리하여 게시글 목록 반환
    @GetMapping(value = "/list_ajax")
    @ResponseBody
    public Map<String, Object> postListAjax(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "A") String category, // 기본값 추가
            @RequestParam(defaultValue = "available") String status, // 기본값 추가
            @RequestParam(defaultValue = "") String search_word
    ) {

        int listcount = postService.getListCount(category, search_word);
        List<Post> list = postService.getPostList(page, limit, category, status, search_word);

        PaginationResult result = new PaginationResult(page, limit, listcount);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page", page);
        map.put("maxpage", result.getMaxpage());
        map.put("startpage", result.getStartpage());
        map.put("endpage", result.getEndpage());
        map.put("listcount", listcount);
        map.put("postlist", list);
        map.put("limit", limit);
        map.put("pagination", result);
        map.put("category", category);
        map.put("status", status);
        map.put("search_word", search_word);
        return map;
    }




    //글쓰기
    @GetMapping(value = "/write")// /board/write
    public String postWrite() {
        return "post/post_write";
    }


    /*
    스프링 컨테이너는 매개변수Board객체를 생성하고 Board객체의 setter 메서드들을 호출하여
    사용자 입력값을 설정합니다.
    매개변수의 이름과 setter의 property가 일치하면 됩니다.
    */



    @PostMapping("/add")
    public String add(Post post, HttpServletRequest request)
            throws Exception {

        //String saveFolder = request.getSession().getServletContext().getRealPath("resources/upload");
        MultipartFile uploadfile = post.getUploadfile();

        if (uploadfile != null && !uploadfile.isEmpty()) {
            String fileDBName = postService.saveUploadFile(uploadfile, SAVE_FOLDER);
            post.setPost_file(fileDBName); //바뀐 파일명으로 저장
            post.setPost_original(uploadfile.getOriginalFilename()); // 원래 파일명 저장
        }

        if (post.getCategory() == null) {
            post.setCategory("A"); // 기본값 설정
        }

        if (post.getPrice() == null) {
            post.setPrice(0); // 기본값 설정
        }


        // 카테고리 처리
        if ("B".equals(post.getCategory())) {
            //post.setPrice(post.getPrice()); // 가격이 입력되었을 경우 유지
        } else {
            post.setPrice(0); // 기타 카테고리는 가격을 0으로 설정
        }

        // 거래상태 처리
        if ("completed".equals(post.getStatus())) {
            post.setStatus(post.getStatus()); //
        } else {
            post.setStatus("available"); //
        }

        postService.insertPost(post); // 저장메서드 호출

        logger.info("Post added: " + post.toString()); //selectKey로 정의한 BOARD_NUM 값 확인해 봅니다.
        return "redirect:list";
    }


    //detail?num=9요청시 파라미터 num의 값을 int num에 저장합니다.
    @GetMapping("/detail")
    public ModelAndView Detail(
            int num, ModelAndView mv,
            HttpServletRequest request,
            @RequestHeader(value = "referer", required = false) String beforeURL, HttpSession session) {
        /*
            1. String beforeURL = request.getHeader("referer"); 의미로
                어느 주소에서 detail로 이동했는지 header의 정보 중에서 "referer"를 통해 알 수 있습니다.
            2. 수정 후 이곳으로 이동하는 경우 조회수는 증가하지 않도록 합니다.
            3. myhome4/board/list에서 제목을 클릭한 경우 조회수가 증가하도록 합니다.
        */
        String sessionReferer = (String) session.getAttribute("referer");
        logger.info("referer:" + beforeURL);

        // 게시글 조회수 증가 (리스트에서 상세보기로 들어왔을 때만)
        if (sessionReferer != null && sessionReferer.equals("list")) {
            //if (beforeURL != null && beforeURL.endsWith("list")) {  // << 주소 경로 바꾼거 땜에
            //if (beforeURL != null && beforeURL.endsWith("list")) {  // << 주소 경로 바꾼거 땜에
            if (beforeURL != null) {
                postService.setReadCountUpdate(num);
            }
            session.removeAttribute("referer");

        }

        // 상세 게시글 정보 가져오기
        Post post = postService.getDetail(num);
        //board=null; //error 페이지 이동 확인하고자 임의로 지정합니다.
        if (post == null) {
            logger.info("상세보기 실패");
            mv.setViewName("error/error");
            mv.addObject("url", request.getRequestURL());
            mv.addObject("message", "상세보기 실패입니다.");
        } else {
            logger.info("상세보기 성공");
            //int count = postCommentService.getListCount(num);
            //int count = commentService.getListCount(num);
            // int count=0;
            mv.setViewName("post/post_view");
            //mv.addObject("count", count);
            mv.addObject("postdata", post);


        }
        return mv;
    }


    @GetMapping("/modifyView")
    public ModelAndView PostModifyView(
            int num,
            ModelAndView mv,
            HttpServletRequest request
    ) {
        Post postdata = postService.getDetail(num);

        //글 내용 불러오기 실패한 경우입니다.
        if (postdata == null) {
            logger.info("수정보기 실패");
            mv.setViewName("error/error");
            mv.addObject("url", request.getRequestURL());
            mv.addObject("message", "수정보기 실패입니다.");
        } else {
            logger.info("(수정)상세보기 성공");
            //수정 폼 페이지로 이동할 때 원문 글 내용을 보여주기 때문에 boarddata 객체를
            //ModelAndView 객체에 저장합니다.
            mv.addObject("postdata", postdata);
            //글 수정 폼 페이지로 이동하기 위해 경로를 설정합니다.
            mv.setViewName("post/post_modify");
        }
        return mv;
    }


    @PostMapping("/modifyAction")
    public String PostModifyAction(
            Post postdata,
            @RequestParam(value = "check", required = false) String check,  // 기존 파일명 유지 여부
            @RequestParam(value = "existingFilePath", required = false) String existingFilePath,  // 기존 파일 경로
            @RequestParam(value = "existingFileName", required = false) String existingFileName,  // 기존 파일명
            @RequestParam(value = "remove_file", required = false) String removeFile,  // 파일 삭제 여부
            Model mv,
            HttpServletRequest request,
            RedirectAttributes rattr
    ) throws Exception {
        boolean usercheck = postService.isPostWriter(postdata.getPost_idx());
        //String saveFolder = request.getSession().getServletContext().getRealPath("resources/upload");
        String url="";
        MultipartFile uploadfile = postdata.getUploadfile();

        // 업로드 폴더 경로 설정
        String saveFolder = SAVE_FOLDER;  // 실제 경로로 변경
        //String saveFolder = request.getSession().getServletContext().getRealPath("resources/upload");

        //System.out.println("Received status: " + postdata.getStatus()); // 디버깅용 로그

        // 파일 삭제 요청이 있는 경우
        if ("true".equals(removeFile) && existingFilePath != null && !existingFilePath.isEmpty()) {
            File fileToDelete = new File(saveFolder, existingFilePath);
            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    logger.info("기존 파일 삭제 완료: " + existingFilePath);
                } else {
                    logger.warn("기존 파일 삭제 실패: " + existingFilePath);
                }
            }
            postdata.setPost_file("");      // DB의 파일 경로 초기화
            postdata.setPost_original("");  // DB의 원본 파일명 초기화
        } else if (check != null && !check.equals("")) { //기존파일 그대로 사용하는 경우
            logger.info("기존파일 그대로 사용합니다.");
//            postdata.setPost_original(check);
            postdata.setPost_file(existingFilePath);  // 기존 파일 경로 유지
            postdata.setPost_original(existingFileName);  // 기존 파일명 유지

            //<input type="hidden" name="BOARD_FILE" value="${boarddata.BOARD_FILE}">
            //위 문장 때문에 board.setBOARD_FILE()값은 자동 저장됩니다.
        } else {
            //답변글의 경우 파일 첨부에 대한 기능이 없습니다.
            //만약 답변글을 수정할 경우
            //<input type="file" id="upfile" name="uploadfile" > 엘리먼트가 존재하지 않아
            //private MultipartFile uploadfile;에서 uploadfile은 null입니다.
            if (uploadfile != null && !uploadfile.isEmpty()) {
                logger.info("파일 변경되었습니다.");
                String fileDBName = postService.saveUploadFile(uploadfile, saveFolder);
                postdata.setPost_file(fileDBName);
                postdata.setPost_original(uploadfile.getOriginalFilename());
            } else if (existingFilePath != null && !existingFilePath.isEmpty()) {
                // 파일을 삭제하지 않고 유지하는 경우
                logger.info("파일 변경 없음, 기존 파일 유지.");
                postdata.setPost_file(existingFilePath);
                postdata.setPost_original(existingFileName);
            } else { // 기존 파일이 없는데 파일 선택하지 않은 경우 또는 기존 파일이 있었는데 삭제한 경우
                logger.info("첨부된 파일 없음");
                postdata.setPost_file(""); // ""로 초기화 합니다.
                postdata.setPost_original(""); // ""로 초기화 합니다.
            } //else end
        } //else end

        // DAO에서 수정 메서드 호출하여 수정합니다.
        int result = postService.postModify(postdata);

        // 수정에 실패한 경우
        if (result == 0) {
            logger.info("게시판 수정 실패");
            mv.addAttribute("url", request.getRequestURL());
            mv.addAttribute("message", "게시판 수정 실패");
            url = "error/error";
        } else { // 수정 성공의 경우
            logger.info("게시판 수정 완료");
            //수정한 글 내용을 보여주기 위해 글 내용 보기 보기 페이지로 이동하기 위해 경로를 설정합니다.
            url = "redirect:detail";
            rattr.addAttribute("num", postdata.getPost_idx());
        }
        return url;
    }



    @PostMapping("/delete")
    public String PostDelete(
            @RequestParam("num") int num,
            Model mv,
            RedirectAttributes rattr,
            HttpServletRequest request
    ) {

        // 글 삭제 명령을 요청한 사용자가 글을 작성한 사용자인지 판단하기 위해
        // 입력한 비밀번호와 저장된 비밀번호를 비교하여 일치하면 삭제합니다.
        //boolean usercheck = postService.isPostWriter(num);

        // 삭제 처리 합니다.
        int result = postService.postDelete(num);

        //삭제 처리 실패한 경우
        if (result == 0) {
            logger.info("게시판 삭제 실패");
            mv.addAttribute("url", request.getRequestURL());
            mv.addAttribute("message", "삭제 실패");
            return "error/error";
        } else {
            // 삭제 처리 성공한 경우 - 글 목록 보기 요청을 전송하는 부분입니다.
            logger.info("삭제 성공");
            rattr.addFlashAttribute("result", "deleteSuccess");
            return "redirect:list";
        }
    }


    @ResponseBody
    @PostMapping("/down")
    public byte[] BoardFileDown(String filename,
                                HttpServletRequest request,
                                String original,
                                HttpServletResponse response) throws Exception {
        //String savePath = "resources/upload";
        // 서블릿의 실행 환경 정보를 담고 있는 객체를 리턴합니다.
        //ServletContext context = request.getSession().getServletContext();
        //String sDownloadPath = context.getRealPath(savePath);

        //수정
        String sFilePath = SAVE_FOLDER + filename;

        File file = new File(sFilePath);

        //org.springframework.util.FileCopyUtils.copyToByteArray(File file) - File객체를 읽어서 바이트 배열로 반환합니다..
        byte[] bytes = FileCopyUtils.copyToByteArray(file);

        String sEncoding = new String(original.getBytes("utf-8"), "ISO-8859-1");
        //Content-Disposition: attachment:브라우저는 해당 Content를 처리하지 않고, 다운로드 하게 됩니다..
        response.setHeader("Content-Disposition", "attachment;filename=" + sEncoding);

        response.setContentLength(bytes.length);
        return bytes;
    }



    //completed >> available 로
    @GetMapping(value="/setAvailable")
    public String setAvailable(int post_idx){
        postService.setAvailable(post_idx);
        return "redirect:/post/detail?num=" + post_idx;
    }


    //available >> completed 로
    @GetMapping(value="/setCompleted")
    public String setCompleted(int post_idx){
        postService.setCompleted(post_idx);
        return "redirect:/post/detail?num=" + post_idx;
    }




}
