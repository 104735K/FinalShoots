package com.Shoots.controller;

import com.Shoots.domain.*;
import com.Shoots.service.KakaoService;
import com.Shoots.service.RegularUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class SocialLoginController {
    @Value("${naver.client_id}")
    private String naver_client_id;

    @Value("${naver.client_secret}")
    private String naver_client_secret;

    private final KakaoService kakaoService;
    private final RegularUserService regularUserService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(SocialLoginController.class);

    @GetMapping("/kakaoCallback")
    public ResponseEntity<?> kakoCallback(@RequestParam("code") String code, HttpServletRequest request, Authentication authentication) {
        HttpSession session = request.getSession();

        // 카카오에서 액세스 토큰과 리프레시 토큰을 가져옴
        KakaoTokenResponseDto tokenResponse = kakaoService.getAccessTokenFromKakao(code);
        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();

        // 카카오 액세스 토큰을 세션에 저장 : 로그아웃 할때 토큰을 만료시키기 위함.
        session.setAttribute("kakaoAccessToken", accessToken);

        /* 사용자 정보를 가져오는 코드. 카카오 로그인 유저는 Shoots 앱에 한번이라도 로그인 한 적이 있다면 해당 로컬에서 처음 가입한다 할지라도
        Shoots app에 이미 인증받은 적이 있는 (카카오 인증 ID) 유저이기에 정보 동의 절차를 받지 않음.
        단, 로컬 DB에는 데이터 저장을 한 적이 없는 유저기에 DB에 저장하는 절차를 밟음.
        차후 배포한 뒤엔 원격 서버에서 한번에 DB가 관리 되기 때문에 DB저장 처리가 아닌 바로 기존 사용자 로그인 처리를 함.
         */
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        // DB에서 유저를 확인하고, 없다면 신규 회원가입
        RegularUser existingUser = regularUserService.findByKakaoUserId(String.valueOf(userInfo.getId())); // 카카오 ID(고유번호)로 조회
        if (existingUser != null) {// 기존 사용자 로그인

            //로그인 유저에게 스프링 시큐리티 권한을 줘야하는데 우리 프로젝트에서 권한을 줄때 기존 스프링에서 사용 하는 방법 (접두사 ROLE_)을 사용하지 않기 때문에 프로젝트의 권한방법과 맞추기 위한 코드
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(existingUser.getRole()));

            // Spring Security 인증 처리
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(existingUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            //로그인을 한 사람의 인증정보가 사라지지 않게 세션에 저장. 아래 코드 없으면 인증정보 받아와도 저장이 안돼서 위의 인증처리 코드가 끝난 직후 다시 인증정보가 사라져서 권한이 사라지는 일이 발생.
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            session.setAttribute("idx", existingUser.getIdx());
            session.setAttribute("id", existingUser.getUser_id());
            session.setAttribute("role", existingUser.getRole());
            session.setAttribute("usertype", "A");

            //이 부분 if / else문으로 분기 나눠서 existingUser.getJumin getGender 써가지고 쟤네가 null 이면
            // /Shoots/myPage/info 로 리다이렉트, 둘 다 null 아니면 mainBefore로 리다이렉트.
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/Shoots/mainBefore")
                    .build();

        } else { //신규 유저인 경우.
            //자동 회원가입 처리를 위한 정보 삽입
            RegularUser regularUser = new RegularUser();
            regularUser.setUser_id("k_" + userInfo.getId()); //회원 id, 카카오 ID (userinfo.id)는 고유 번호라 중복 걱정X
            regularUser.setName(userInfo.getKakaoAccount().getProfile().getNickName()); //회원이름
            regularUser.setEmail(userInfo.getKakaoAccount().getEmail()); //이메일에 카카오 이메일 삽입

            //난수 5자리를 암호화 후 비밀번호로 설정. 어차피 카카오 로그인은 회원이 있는지만 판별한뒤 자동 로그인이기 때문에 비밀번호 쓸모 x
            Random random = new Random();
            int randomNumber = random.nextInt(100000);
            regularUser.setPassword(passwordEncoder.encode((String.valueOf(randomNumber))));
            regularUserService.insert2(regularUser);

            // 위까지 프로젝트에 DB 처리, 아래부터 if문 (=기존유저 있을때) 에서 했던 로그인 처리 그대로 따옴.

            //로그인 유저에게 스프링 시큐리티 권한을 줘야하는데 우리 프로젝트에서 권한을 줄때 기존 스프링에서 사용 하는 방법 (접두사 ROLE_)을 사용하지 않기 때문에 프로젝트의 권한방법과 맞추기 위한 코드
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(regularUser.getRole()));

            // Spring Security 인증 처리
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(regularUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            //로그인을 한 사람의 인증정보가 사라지지 않게 세션에 저장. 아래 코드 없으면 인증정보 받아와도 저장이 안돼서 위의 인증처리 코드가 끝난 직후 다시 인증정보가 사라져서 권한이 사라지는 일이 발생.
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            session.setAttribute("idx", regularUser.getIdx());
            session.setAttribute("id", regularUser.getUser_id());
            session.setAttribute("role", regularUser.getRole());
            session.setAttribute("usertype", "A");

            //이 부분 if / else문으로 분기 나눠서 regularUser.getJumin getGender 써가지고 쟤네가 null 이면
            // /Shoots/myPage/info 로 리다이렉트, 둘 다 null 아니면 mainBefore로 리다이렉트.
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/Shoots/mainBefore")
                    .build();
        }
    } //kakaoCallback

@PostMapping("/googleCallback")
    public ModelAndView googleCallback(ModelAndView mv,@RequestParam String googleAuId,
                                       @RequestParam String googleName,
                                       @RequestParam String googleEmail,
                                       HttpSession session, HttpServletRequest request) {
        // 받은 정보 사용
        logger.info("구글 고유 인증번호 : " + googleAuId);
        logger.info("구글 Name: " + googleName);
        logger.info("구글 Email: " + googleEmail);

        // DB에서 유저를 확인하고, 없다면 신규 회원가입
        RegularUser existingUser = regularUserService.findByGoogleUserId(googleAuId); // 구글 ID(고유번호)로 조회
        if (existingUser != null) {// 기존 사용자 로그인

            //로그인 유저에게 스프링 시큐리티 권한을 줘야하는데 우리 프로젝트에서 권한을 줄때 기존 스프링에서 사용 하는 방법 (접두사 ROLE_)을 사용하지 않기 때문에 프로젝트의 권한방법과 맞추기 위한 코드
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(existingUser.getRole()));

            // Spring Security 인증 처리
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(existingUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            //로그인을 한 사람의 인증정보가 사라지지 않게 세션에 저장. 아래 코드 없으면 인증정보 받아와도 저장이 안돼서 위의 인증처리 코드가 끝난 직후 다시 인증정보가 사라져서 권한이 사라지는 일이 발생.
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            session.setAttribute("idx", existingUser.getIdx());
            session.setAttribute("id", existingUser.getUser_id());
            session.setAttribute("role", existingUser.getRole());
            session.setAttribute("usertype", "A");

        } else { //신규 유저인 경우.
            //자동 회원가입 처리를 위한 정보 삽입
            RegularUser regularUser = new RegularUser();
            regularUser.setUser_id("g_" + googleAuId); //구글 고유 인증번호를 ID로 씀.
            regularUser.setName(googleName); //회원이름
            regularUser.setEmail(googleEmail); //이메일에 구글 이메일 삽입

            //난수 5자리를 암호화 후 비밀번호로 설정. 어차피 카카오 로그인은 회원이 있는지만 판별한뒤 자동 로그인이기 때문에 비밀번호 쓸모 x
            Random random = new Random();
            int randomNumber = random.nextInt(100000);
            regularUser.setPassword(passwordEncoder.encode((String.valueOf(randomNumber))));
            regularUserService.insert2(regularUser);

            // 위까지 프로젝트에 DB 처리, 아래부터 if문 (=기존유저 있을때) 에서 했던 로그인 처리 그대로 따옴.

            //로그인 유저에게 스프링 시큐리티 권한을 줘야하는데 우리 프로젝트에서 권한을 줄때 기존 스프링에서 사용 하는 방법 (접두사 ROLE_)을 사용하지 않기 때문에 프로젝트의 권한방법과 맞추기 위한 코드
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(regularUser.getRole()));

            // Spring Security 인증 처리
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(regularUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            //로그인을 한 사람의 인증정보가 사라지지 않게 세션에 저장. 아래 코드 없으면 인증정보 받아와도 저장이 안돼서 위의 인증처리 코드가 끝난 직후 다시 인증정보가 사라져서 권한이 사라지는 일이 발생.
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            session.setAttribute("idx", regularUser.getIdx());
            session.setAttribute("id", regularUser.getUser_id());
            session.setAttribute("role", regularUser.getRole());
            session.setAttribute("usertype", "A");
        }

        mv.setViewName("redirect:/mainBefore");
        return mv;
    } //googleCallback 끝


    @GetMapping("/naverCallback")
    public ResponseEntity<?> naverCallback(@RequestParam String code, @RequestParam String state, HttpSession session, Authentication authentication) throws JsonProcessingException {
        // 네이버 API에 요청할 파라미터 세팅
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naver_client_id);
        params.add("client_secret", naver_client_secret);
        params.add("code", code);
        params.add("state", state);

        // HTTP 요청 객체 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(params, headers);

        //네이버 API 요청 보내기
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> tokenResponse = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        // 응답 JSON을 파싱하여 액세스 토큰 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(tokenResponse.getBody());
        String accessToken = jsonNode.get("access_token").asText();

        //네이버 로그아웃처리를 하기위해 토큰을 세션에 저장해둠
        session.setAttribute("naverAccessToken", accessToken);


        // 액세스 토큰을 사용하여 사용자 정보 가져오기
        String userInfo = getUserProfile(accessToken);

        ObjectMapper om = new ObjectMapper();
        JsonNode jn = om.readTree(userInfo); // JSON 문자열을 JsonNode로 변환

// "response" 필드 내부 데이터 가져오기
        JsonNode responseNode = jn.get("response");

        String naverAuId = responseNode.get("id").asText();
        String name = responseNode.get("name").asText();
        String email = responseNode.get("email").asText();
        String gender = responseNode.get("gender").asText();
        String mobile = responseNode.get("mobile").asText().replaceAll("-", "");
        String birthday = responseNode.get("birthday").asText().replaceAll("-", "");
        String birthyear = responseNode.get("birthyear").asText().substring(2,4);
        String jumin = birthyear + birthday;

        // DB에서 유저를 확인하고, 없다면 신규 회원가입
        RegularUser existingUser = regularUserService.findByNaverAuId(naverAuId);

        if (existingUser != null) {// 기존 사용자 로그인

            //로그인 유저에게 스프링 시큐리티 권한을 줘야하는데 우리 프로젝트에서 권한을 줄때 기존 스프링에서 사용 하는 방법 (접두사 ROLE_)을 사용하지 않기 때문에 프로젝트의 권한방법과 맞추기 위한 코드
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(existingUser.getRole()));

            // Spring Security 인증 처리
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(existingUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            //로그인을 한 사람의 인증정보가 사라지지 않게 세션에 저장. 아래 코드 없으면 인증정보 받아와도 저장이 안돼서 위의 인증처리 코드가 끝난 직후 다시 인증정보가 사라져서 권한이 사라지는 일이 발생.
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            session.setAttribute("idx", existingUser.getIdx());
            session.setAttribute("id", existingUser.getUser_id());
            session.setAttribute("role", existingUser.getRole());
            session.setAttribute("usertype", "A");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/Shoots/mainBefore")
                    .build();

        } else { //신규 유저인 경우.
            //자동 회원가입 처리를 위한 정보 삽입
            RegularUser regularUser = new RegularUser();
            regularUser.setUser_id("n_" + naverAuId); //네이버 인증Id로 db id 삽입
            regularUser.setName(name); //회원이름
            regularUser.setEmail(email); //이메일에 네이버 이메일 삽입
            regularUser.setTel(mobile);
            regularUser.setJumin(jumin);

            if (gender != null && gender.equals("M")) {
                regularUser.setGender(1);
            } else if (gender != null && gender.equals("F")) {
                regularUser.setGender(2);
            }

            //난수 5자리를 암호화 후 비밀번호로 설정. 어차피 소셜 로그인은 회원이 있는지만 판별한뒤 자동 로그인이기 때문에 비밀번호 쓸모 x
            Random random = new Random();
            int randomNumber = random.nextInt(100000);
            regularUser.setPassword(passwordEncoder.encode((String.valueOf(randomNumber))));
            regularUserService.insert3(regularUser);

            // 위까지 프로젝트에 DB 처리, 아래부터 if문 (=기존유저 있을때) 에서 했던 로그인 처리 그대로 따옴.

            //로그인 유저에게 스프링 시큐리티 권한을 줘야하는데 우리 프로젝트에서 권한을 줄때 기존 스프링에서 사용 하는 방법 (접두사 ROLE_)을 사용하지 않기 때문에 프로젝트의 권한방법과 맞추기 위한 코드
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(regularUser.getRole()));

            // Spring Security 인증 처리
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(regularUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            //로그인을 한 사람의 인증정보가 사라지지 않게 세션에 저장. 아래 코드 없으면 인증정보 받아와도 저장이 안돼서 위의 인증처리 코드가 끝난 직후 다시 인증정보가 사라져서 권한이 사라지는 일이 발생.
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            session.setAttribute("idx", regularUser.getIdx());
            session.setAttribute("id", regularUser.getUser_id());
            session.setAttribute("role", regularUser.getRole());
            session.setAttribute("usertype", "A");

            //이 부분 if / else문으로 분기 나눠서 regularUser.getJumin getGender 써가지고 쟤네가 null 이면
            // /Shoots/myPage/info 로 리다이렉트, 둘 다 null 아니면 mainBefore로 리다이렉트.
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/Shoots/mainBefore")
                    .build();
        }
    } //naverCallback


    private String getUserProfile(String accessToken) { //토큰으로 네이버 유저 정보 받아오기
        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        // 요청 생성
        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();

        //네이버 사용자 정보 API 호출
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                request,
                String.class
        );

        //응답 JSON 리턴
        return response.getBody();
    }





}
