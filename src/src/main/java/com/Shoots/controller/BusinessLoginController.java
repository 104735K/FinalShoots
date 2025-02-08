package com.Shoots.controller;

import com.Shoots.domain.BusinessUser;
import com.Shoots.domain.MailVO;
import com.Shoots.redis.RedisService;
import com.Shoots.service.BusinessUserService;
import com.Shoots.task.SendMail;
import com.Shoots.task.SendMailText;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class BusinessLoginController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessLoginController.class);
    private final BusinessUserService businessUserService;
    private BCryptPasswordEncoder passwordEncoder;
    private SendMailText sendMail;

    private final RedisService redisService;

    public BusinessLoginController(BusinessUserService businessUserService, BCryptPasswordEncoder passwordEncoder, SendMailText sendMail, RedisService redisService) {
        this.businessUserService = businessUserService;
        this.passwordEncoder = passwordEncoder;
        this.sendMail = sendMail;
        this.redisService = redisService;
    }


    @GetMapping(value = "/businessLogin")
    public ModelAndView login(ModelAndView mv, @CookieValue(value = "remember-me", required = false) Cookie readCookie,
                              HttpSession session, Principal userPrincipal) {
        session.removeAttribute("verifyNumber"); //비밀번호 찾을때 저장했던 인증번호 session을 지움
        session.removeAttribute("promptId"); //비밀번호 변경때 사용한 임시 id session을 지움


        if (userPrincipal != null) { // 로그인 상태면 강제로 main으로 보냄
            logger.info("저장된 아이디 : " + userPrincipal.getName());
            mv.setViewName("redirect:/main");
        } else { // 로그인 안된 상태면 로그인 폼 뜸
            mv.setViewName("home/loginForm");
            mv.addObject("loginResult", session.getAttribute("loginResult"));
            session.removeAttribute("loginResult");
        }

        return mv;
    }


    @GetMapping("/businessJoinForm")
    public String getBusinessJoinForm(Model model) {
        return "fragments/businessJoinForm";
    }

    @PostMapping(value = "/businessJoinProcess")
    public String businessJoinProcess(BusinessUser user, RedirectAttributes rattr,
                                     Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

        ModelAndView mv = new ModelAndView();
        response.setContentType("text/html;charset=UTF-8");

        //비밀번호 암호화 추가
        String encPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        int result = businessUserService.insert(user);
        PrintWriter out = response.getWriter();

        //삽입 성공하면?
        if (result == 1) {

            BusinessUser savedUser = businessUserService.getBusinessUserAddressById(user.getBusiness_id());
            redisService.saveAddressData(savedUser.getBusiness_idx(), savedUser.getAddress());

            out.println("<script type='text/javascript'>");
            out.println("alert('기업회원가입에 성공하셨습니다!');");
            out.println("window.location.href='/Shoots/login';");
            out.println("</script>");
            return null;
        } else { //db에  insert 실패하면?
            out.println("<script type='text/javascript'>");
            out.println("alert('기업회원가입에 실패했습니다.');");
            out.println("</script>");
            return null;
        }
    } //BusinessJoinProcess 끝

    @ResponseBody
    @GetMapping(value = "/business_idcheck")
    public int business_idcheck(String id) {
        return businessUserService.selectById(id);
    }

    @ResponseBody
    @GetMapping(value = "/business_emailcheck")
    public int business_emailcheck(String email) {
        return businessUserService.selectByEmail(email);
    }

    @GetMapping(value = "/findBusinessId")
    public String findId() {
        return "home/findBusinessIdForm";
    }


    @PostMapping(value = "/findBusinessIdProcess")
    public String findBusinessIdProcess(String email, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        BusinessUser user = businessUserService.findIdWithEmail(email);


        if (user == null) {
            out.println("<script type='text/javascript'>");
            out.println("alert('일치하는 이메일이 없습니다. 이메일을 확인해 주세요.')");
            out.println("location.href='/Shoots/findBusinessId';");
            out.println("</script>");
            out.flush();
        } else {
            MailVO vo = new MailVO();
            vo.setTo(user.getEmail());
            vo.setSubject("Shoots에서 회원님의 기업 아이디를 전달해 드립니다.");
            vo.setText("회원님의 기업 아이디입니다 : " + user.getBusiness_id());
            sendMail.sendMail(vo);

            out.println("<script type='text/javascript'>");
            out.println("alert('이메일로 회원님의 기업 아이디를 전달했습니다.')");
            out.println("location.href='/Shoots/login';");
            out.println("</script>");
            out.flush();
        }
        return null;
    }

    @GetMapping(value = "/findBusinessPassword")
    public String findBusinessPassword() {
        return "home/findBusinessPasswordForm";
    }

    @ResponseBody
    @PostMapping("/checkBusinessUserWithIdAndEmail")
    public Map<String, Object> checkBusinessUserWithIdAndEmail(@RequestBody Map<String, String> params, HttpSession session) {
        session.removeAttribute("verifyNumber");
        session.removeAttribute("promptId");
        String businessId = params.get("business_id");
        String email = params.get("email");

        BusinessUser user = businessUserService.selectWithIdAndEmail(businessId, email);
        Map<String, Object> response = new HashMap<>();


        if (user != null) { //유저 데이터가 있으면 ?
            // 6자리 난수 생성
            String verifyNumber = generateVerificationNumber();

            // 이메일 전송
            MailVO vo = new MailVO();
            vo.setFrom("chldudtn0206@naver.com");
            vo.setTo(email);
            vo.setSubject("Shoots에서 보낸 인증번호 입니다.");
            vo.setText("인증번호는 \" " + verifyNumber + " \" 입니다.");
            sendMail.sendMail(vo); // 메일 전송
            session.setAttribute("verifyNumber", verifyNumber); //인증번호를 잠시 세션에 저장
            session.setAttribute("promptId", businessId); //비밀번호 찾기에서 비밀번호 변경할때 커리문으로 쓰기 위해 user_id를 받아둠
            logger.info(verifyNumber + " : 메일로 인증번호");
            logger.info("세션에 저장된 promptId: " + session.getAttribute("promptId"));

            response.put("success", true);
            response.put("verifyNumber", verifyNumber); // 클라이언트에 난수를 전달
        } else {
            response.put("success", false);
        }
        return response;
    }

    // 6자리 난수 생성 메소드
    private String generateVerificationNumber() {
        Random random = new Random();
        int number = random.nextInt(1000000); // 0 ~ 999999
        String formattedNumber = String.format("%06d", number); // 항상 6자리로 포맷팅
        return formattedNumber;
    }

    @PostMapping("/verifyNumberBusinessProcess")
    public String verifyNumberBusinessProcess(@RequestParam String verifyNumber, HttpSession session, HttpServletResponse response) throws IOException {
        String sessionVerifyNumber = (String) session.getAttribute("verifyNumber"); // 세션에서 인증번호 가져오기
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();


        if (sessionVerifyNumber != null && sessionVerifyNumber.equals(verifyNumber)) { //이메일 인증번호와 세션에 저장한 인증번호가 같을 때 (=인증완료)
            session.removeAttribute("verifyNumber");
            out.println("<script type='text/javascript'>");
            out.println("alert('인증되었습니다. 비밀번호 변경 페이지로 이동합니다.')");
            out.println("window.location.href = 'BusinessUserPasswordForm';");
            out.println("</script>");
            out.flush();
            response.flushBuffer();
            return null;
        } else {
            out.println("<script type='text/javascript'>");
            out.println("alert('인증번호가 일치하지 않습니다. 다시 입력해주세요.')");
            out.println("history.back();");
            out.println("</script>");
            out.flush();
            response.flushBuffer();
            return null;
        }
    }

    @GetMapping(value = "/BusinessUserPasswordForm")
    String BusinessUserPasswordForm() {
        return "home/updateBusinessUserPasswordForm";
    }

    @PostMapping(value = "/updateBusinessUserPasswordProcess")
    public String updateBusinessUserPasswordProcess(BusinessUser user, Model model,
                                                   HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        //비밀번호 암호화 추가
        String encPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        int result = businessUserService.updateBusinessUserPassword(user); //이 시점에서 db에 정보 변경

        if(result==1){ //성공적으로 db에 정보 업데이트 됐을때
            session.removeAttribute("promptId");
            out.println("<script type='text/javascript'>");
            out.println("alert('비밀번호가 성공적으로 변경됐습니다!')");
            out.println("window.location.href = 'login';");
            out.println("</script>");
        }else{
            out.println("<script type='text/javascript'>");
            out.println("alert('비밀번호 변경에 실패했습니다. 다시 시도해주세요.')");
            out.println("history.back();");
            out.println("</script>");
            logger.info("기업회원 비밀번호 업데이트 실패 (updateBusinessUserPasswordProcess)");
        }
        return null;
    }



}
