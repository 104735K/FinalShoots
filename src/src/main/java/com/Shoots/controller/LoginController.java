package com.Shoots.controller;

import com.Shoots.domain.MailVO;
import com.Shoots.domain.RegularUser;
import com.Shoots.service.RegularUserService;
import com.Shoots.task.SendMailText;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final RegularUserService regularUserService;
    private BCryptPasswordEncoder passwordEncoder;
    private SendMailText sendMail;

    @Value("${kakao.client_id}")
    private String kakao_client_id;

    @Value("${kakao.redirect_uri}")
    private String kakao_redirect_uri;

    @Value("${naver.client_id}")
    private String naver_client_id;

    @Value("${naver.client_secret}")
    private String naver_client_secret;

    @Value("${naver.redirect_uri}")
    private String naver_redirect_uri;



    public LoginController(RegularUserService regularUserService, BCryptPasswordEncoder passwordEncoder, SendMailText sendMail) {

        this.regularUserService = regularUserService;
        this.passwordEncoder = passwordEncoder;
        this.sendMail = sendMail;
    }

    @GetMapping(value = "/login")
    public ModelAndView login(ModelAndView mv, @CookieValue(value = "remember-me", required = false) Cookie readCookie,
                              HttpSession session, HttpServletRequest request, Principal userPrincipal) {
        session.removeAttribute("verifyNumber"); //비밀번호 찾을때 저장했던 인증번호 session을 지움
        session.removeAttribute("promptId"); //비밀번호 변경때 사용한 임시 id session을 지움


        //카카오톡 로그인을 위한 경로 설정.
        String kakaoLoginPath = String.format(
                "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s",
                kakao_client_id, URLEncoder.encode(kakao_redirect_uri, StandardCharsets.UTF_8)
        );
        mv.addObject("kakaoLoginPath", kakaoLoginPath);



        //네이버 로그인을 위한 경로 설정.
        Random random = new Random();
        int state = random.nextInt(10000);
        String naverLoginPath = String.format(
                "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=%s&state=%s&redirect_uri=%s",
                naver_client_id, state,URLEncoder.encode(naver_redirect_uri, StandardCharsets.UTF_8)
        );
        mv.addObject("naverLoginPath", naverLoginPath);

        if (userPrincipal != null) { // 로그인 상태면 강제로 main으로 보냄
            logger.info("저장된 아이디 : " + userPrincipal.getName());
            logger.info("Request URL: " + request.getRequestURL());
            mv.clear(); // 파라미터 초기화
            mv.setViewName("redirect:/main");
        } else { // 로그인 안된 상태면 로그인 폼 뜸
            mv.setViewName("home/loginForm");
            mv.addObject("loginResult", session.getAttribute("loginResult"));
            session.removeAttribute("loginResult");
        }

        return mv;
    }

    @GetMapping(value = "/logout")
    public String logout(HttpSession session, HttpServletResponse resp) throws IOException {

        // 1. 카카오 액세스 토큰 가져오기 (세션 또는 SecurityContextHolder에서 가져올 수 있음)
        String kakaoAccessToken = (String) session.getAttribute("kakaoAccessToken");

        if (kakaoAccessToken != null) {
            // 2. 카카오 API 호출하여 토큰 만료시키기
            try {
                String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout";
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + kakaoAccessToken);

                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.postForEntity(kakaoLogoutUrl, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    logger.info("카카오 로그아웃 성공: " + response.getBody());
                } else {
                    logger.warn("카카오 로그아웃 실패: " + response.getStatusCode());
                }
            } catch (Exception e) {
                logger.error("카카오 로그아웃 요청 중 에러 발생: ", e);
            }
        } else {
            logger.warn("카카오 액세스 토큰이 존재하지 않음. 카카오 로그아웃 생략.");
        }

        //구글 로그아웃 처리를 위한 코드. 구글 로그인 회원은 로그아웃 시 홈페이지 로그아웃 -> 구글 계정 로그아웃 처리로 진행.
        if (session.getAttribute("id") != null && session.getAttribute("id").toString().startsWith("g_")) {
            session.invalidate();
            resp.sendRedirect("https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:1000/Shoots/main");
            return null;
        }


        // 네이버 액세스 토큰 가져오기
        String naverAccessToken = (String) session.getAttribute("naverAccessToken");

        if (naverAccessToken != null) {
            // 네이버 API 호출하여 토큰 만료시키기
            try {
                String naverLogoutUrl = "https://nid.naver.com/oauth2.0/token?grant_type=delete"
                        + "&client_id=" + naver_client_id
                        + "&client_secret=" + naver_client_secret
                        + "&access_token=" + naverAccessToken
                        + "&service_provider=NAVER";

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.getForEntity(naverLogoutUrl, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    logger.info("네이버 로그아웃 성공: " + response.getBody());
                } else {
                    logger.warn("네이버 로그아웃 실패: " + response.getStatusCode());
                }
            } catch (Exception e) {
                logger.error("네이버 로그아웃 요청 중 에러 발생: ", e);
            }
        } else {
            logger.warn("네이버 액세스 토큰이 존재하지 않음. 네이버 로그아웃 생략.");
        }

        /* //기존 네이버 로그아웃 경로는 로그아웃하고 재 로그인 시 개인정보 수집항목 동의를 다시 받아야하는데, 아래의 방법을 사용하면 그냥 세션 로그아웃 처리만 됨.
            if (session.getAttribute("id") != null && session.getAttribute("id").toString().startsWith("n_")) {
            session.invalidate(); // 세션 초기화
            resp.sendRedirect("https://nid.naver.com/nidlogin.logout");
            return null;
        }
        */


        // 세션 무효화 (로그아웃 처리)
        session.invalidate();

        // 로그인 페이지로 리다이렉트
        return "redirect:/login";
    }

    @GetMapping(value = "/join")
    public String join() {
        return "home/joinForm";
    }

    @GetMapping("/regularJoinForm")
    public String getRegularJoinForm(Model model) {
        return "fragments/regularJoinForm";
    }

    @PostMapping(value = "/regularJoinProcess")
    public String regularJoinProcess(RegularUser user, HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        //비밀번호 암호화 추가
        String encPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        int result = regularUserService.insert(user);
        PrintWriter out = response.getWriter();

        //삽입 성공하면?
        if (result == 1) {
            out.println("<script type='text/javascript'>");
            out.println("alert('회원가입에 성공했습니다!');");
            out.println("window.location.href='/Shoots/login';");
            out.println("</script>");
            return null;
        } else { //db에  insert 실패하면?
            out.println("<script type='text/javascript'>");
            out.println("alert('회원가입에 실패했습니다.');");
            out.println("</script>");
            return null;
        }
    } //regularJoinProcess 끝

    @ResponseBody
    @GetMapping(value = "/idcheck")
    public int idcheck(String id) {
        return regularUserService.selectById(id);
    }


    @ResponseBody
    @GetMapping(value = "/emailcheck")
    public int emailcheck(String email) {
        return regularUserService.selectByEmail(email);
    }

    @GetMapping(value = "/findRegularId")
    public String findRegularId() {
        return "home/findRegularIdForm";
    }

    @PostMapping(value = "/findRegularIdProcess")
    public String findRegularIdProcess(String email, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        RegularUser user = regularUserService.findIdWithEmail(email);


        if (user == null) {
            out.println("<script type='text/javascript'>");
            out.println("alert('일치하는 이메일이 없습니다. 이메일을 확인해 주세요.')");
            out.println("location.href='/Shoots/findRegularId';");
            out.println("</script>");
            out.flush();
        } else {
            MailVO vo = new MailVO();
            vo.setTo(user.getEmail());
            vo.setSubject("Shoots에서 회원님의 아이디를 전달해 드립니다.");
            vo.setText("회원님의 아이디입니다 : " + user.getUser_id());
            sendMail.sendMail(vo);

            out.println("<script type='text/javascript'>");
            out.println("alert('이메일로 회원님의 아이디를 전달했습니다.')");
            out.println("location.href='/Shoots/login';");
            out.println("</script>");
            out.flush();
        }
        return null;
    }


    @GetMapping(value = "/findRegularPassword")
    public String findRegularPassword() {
        return "home/findRegularPasswordForm";
    }



    @ResponseBody
    @PostMapping("/checkRegularUserWithIdAndEmail")
    public Map<String, Object> checkRegularUserWithIdAndEmail(@RequestBody Map<String, String> params, HttpSession session) {
        session.removeAttribute("verifyNumber");
        session.removeAttribute("promptId");
        String userId = params.get("user_id");
        String email = params.get("email");

        RegularUser user = regularUserService.selectWithIdAndEmail(userId, email);
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
            session.setAttribute("promptId", userId); //비밀번호 찾기에서 비밀번호 변경할때 커리문으로 쓰기 위해 user_id를 받아둠
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

    @PostMapping("/verifyNumberProcess")
    public String verifyNumberProcess(@RequestParam String verifyNumber, HttpSession session, HttpServletResponse response) throws IOException {
        String sessionVerifyNumber = (String) session.getAttribute("verifyNumber"); // 세션에서 인증번호 가져오기
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();


        if (sessionVerifyNumber != null && sessionVerifyNumber.equals(verifyNumber)) { //이메일 인증번호와 세션에 저장한 인증번호가 같을 때 (=인증완료)
            session.removeAttribute("verifyNumber");
            out.println("<script type='text/javascript'>");
            out.println("alert('인증되었습니다. 비밀번호 변경 페이지로 이동합니다.')");
            out.println("window.location.href = 'regularUserPasswordForm';");
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

    @GetMapping(value = "/regularUserPasswordForm")
    String regularUserPasswordForm() {
        return "home/updateRegularUserPasswordForm";
    }

    @PostMapping(value = "/updateRegularUserPasswordProcess")
    public String updateRegularUserPasswordProcess(RegularUser user, Model model,
                                HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        //비밀번호 암호화 추가
        String encPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        int result = regularUserService.updateRegularUserPassword(user); //이 시점에서 db에 정보 변경

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
            logger.info("개인회원 비밀번호 업데이트 실패 (updateRegularUserPasswordProcess)");
        }
        return null;
    }

}
