package com.Shoots.controller;

import com.Shoots.domain.*;
import com.Shoots.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Locale.filter;


@Controller
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

    private final RegularUserService regularUserService;
    private final PaymentService paymentService;
    private final MatchService matchService;
    private final BcBlacklistService bcBlacklistService;
    private final BusinessUserService businessUserService;
    private final BusinessInfoService businessInfoService;
    private final InquiryService inquiryService;
    private final InquiryCommentService inquiryCommentService;

    @Value("${my.savefolder}")
    private String saveFolder;

    @GetMapping("/dashboardBefore")
    public String beforeBusinessDashboard(@AuthenticationPrincipal Object principal, HttpSession session) {
        if (principal instanceof BusinessUser) {
            BusinessUser businessUser = (BusinessUser) principal;
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String businessDashboard(Model model, HttpSession session,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(required = false) String filter,
                                    @RequestParam(required = false) String gender,
                                    @RequestParam(required = false) String level) {

        Integer business_idx = (Integer) session.getAttribute("idx");

        List<Integer> monthlyData = paymentService.getPlayerCountByMonth(business_idx);
        List<Integer> monthlyMatchData = matchService.getTotalMatchByMonth(business_idx);

        int totalMatch = matchService.getTotalMatchById(business_idx);

        session.setAttribute("refer", "list");

        int limit = 10;
        int listCount = matchService.getListCountById(business_idx);

        List<Match> list = matchService.getMatchListById(business_idx, filter, gender, level, page, limit);

        PaginationResult result = new PaginationResult(page, limit, listCount);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

        int recruitingMatchCount = 0;
        int completedRecruitmentCount = 0;
        List<Integer> monthlyCompletedRecruitmentCount = new ArrayList<>(Collections.nCopies(12, 0));

        for (Match match : list) {

            int playerCount = paymentService.getPlayerCount(match.getMatch_idx());
            match.setPlayerCount(playerCount);

            String formattedDate = match.getMatch_date().format(formatter);
            match.setFormattedDate(formattedDate);

            String a = match.getMatch_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ' ' + match.getMatch_time();

            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime matchDateTime = LocalDateTime.parse(a, formatter1);

            LocalDateTime currentDateTime = LocalDateTime.now();

            LocalDateTime twoHoursBeforeMatch = matchDateTime.minusHours(2);

            boolean isMatchPast = twoHoursBeforeMatch.isBefore(currentDateTime);
            match.setMatchPast(isMatchPast);

            if (!isMatchPast) {
                recruitingMatchCount++;
            }

            if (isMatchPast && playerCount >= match.getPlayer_min()) {
                completedRecruitmentCount++;

                int month = match.getMatch_date().getMonthValue();
                monthlyCompletedRecruitmentCount.set(month - 1, monthlyCompletedRecruitmentCount.get(month - 1) + 1);
            }
        }

        model.addAttribute("monthlyData", monthlyData);
        model.addAttribute("totalMatch", totalMatch);

        model.addAttribute("recruitingMatchCount", recruitingMatchCount);
        model.addAttribute("completedRecruitmentCount", completedRecruitmentCount);

        model.addAttribute("monthlyMatchData", monthlyMatchData);
        model.addAttribute("monthlyCompletedRecruitmentCount", monthlyCompletedRecruitmentCount); // 월별 확정된 매칭글

        return "business/businessDashboard";
    }

    @GetMapping("/post")
    public ModelAndView businessPost(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(required = false) String filter,
                                     @RequestParam(required = false) String gender,
                                     @RequestParam(required = false) String level,
                               ModelAndView modelAndView, HttpSession session) {

        Integer idx = (Integer) session.getAttribute("idx");

        session.setAttribute("refer", "list");

        int limit = 20;
        int listCount = matchService.getListCountById(idx);

        List<Match> list = matchService.getMatchListById(idx, filter, gender, level, page, limit);

        PaginationResult result = new PaginationResult(page, limit, listCount);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

        for (Match match : list) {

            int playerCount = paymentService.getPlayerCount(match.getMatch_idx());
            match.setPlayerCount(playerCount);

            String formattedDate = match.getMatch_date().format(formatter);
            match.setFormattedDate(formattedDate);

            String a = match.getMatch_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ' ' + match.getMatch_time();

            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime matchDateTime = LocalDateTime.parse(a, formatter1);

            LocalDateTime currentDateTime = LocalDateTime.now();

            LocalDateTime twoHoursBeforeMatch = matchDateTime.minusHours(2);

            boolean isMatchPast = twoHoursBeforeMatch.isBefore(currentDateTime);
            match.setMatchPast(isMatchPast);

        }

        modelAndView.setViewName("business/businessMatchPost");
        modelAndView.addObject("page", page);
        modelAndView.addObject("maxpage", result.getMaxpage());
        modelAndView.addObject("startpage", result.getStartpage());
        modelAndView.addObject("endpage", result.getEndpage());
        modelAndView.addObject("listcount", listCount);
        modelAndView.addObject("matchList", list);
        modelAndView.addObject("limit", limit);

        return modelAndView;
    }

    @GetMapping("/postWrite")
    public ModelAndView matchWrite(HttpSession session, ModelAndView modelAndView) {

        Integer business_idx = (Integer) session.getAttribute("idx");
        BusinessInfo businessInfo = businessInfoService.getInfoById(business_idx);

        modelAndView.setViewName("business/businessMatchForm");
        modelAndView.addObject("businessInfo", businessInfo);
        return modelAndView;
    }

    @PostMapping("/postAdd")
    public String matchAdd(Match match) {

        matchService.insertMatch(match);
        logger.info(match.toString());

        return "redirect:dashboard?tab=matchPost";
    }

    @GetMapping("/postDetail")
    public ModelAndView postDetail(int match_idx, ModelAndView modelAndView, HttpServletRequest request) {

        Match match = matchService. getDetail(match_idx);
        BusinessInfo businessInfo = businessInfoService.getInfoById(match.getWriter());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        DateTimeFormatter formatterT = DateTimeFormatter.ofPattern("HH시 mm분");

        // 날짜 포맷
        String formattedDate = match.getMatch_date().format(formatter);
        match.setFormattedDate(formattedDate);

        // 시간 포맷
        String formattedTime = match.getMatch_time().format(formatterT);
        match.setFormattedTime(formattedTime);

        // isMatchPast
        String a = match.getMatch_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ' ' + match.getMatch_time();

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime matchDateTime = LocalDateTime.parse(a, formatter1);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime twoHoursBeforeMatch = matchDateTime.minusHours(2);

        boolean isMatchPast = twoHoursBeforeMatch.isBefore(currentDateTime);
        match.setMatchPast(isMatchPast);

        logger.info("isMatchPast : " + match.isMatchPast());

        int playerCount = paymentService.getPlayerCount(match_idx);
        logger.info("신청 플레이어 수 playerCount : " + playerCount);

        if (match == null) {
            logger.info("상세보기 실패");

            modelAndView.setViewName("error/error");
            modelAndView.addObject("url", request.getRequestURL());
            modelAndView.addObject("message", "상세보기 실패");
        } else {
            logger.info("상세보기 성공");

            modelAndView.setViewName("business/businessMatchDetail");
            modelAndView.addObject("match", match);
            modelAndView.addObject("playerCount", playerCount);
            modelAndView.addObject("businessInfo", businessInfo);
        }
        return modelAndView;
    }

    @GetMapping("/postUpdateForm")
    public ModelAndView postUpdateForm(int match_idx, ModelAndView modelAndView, HttpServletRequest request) {
        Match match = matchService. getDetail(match_idx);

        if(match == null) {
            logger.info("수정 보기 실패");
            modelAndView.setViewName("error/error");
            modelAndView.addObject("url", request.getRequestURL());
            modelAndView.addObject("message", "수정보기 실패입니다");
        } else {
            logger.info("(수정)상세보기 성공");
            modelAndView.addObject("match", match);
            modelAndView.setViewName("business/businessMatchUpdateForm");
        }
        return modelAndView;
    }

    @PostMapping("/postUpdate")
    public String postUpdate(Match match, Model model, HttpServletRequest request, RedirectAttributes rattr) {

        int result = matchService.updateMatch(match);

        logger.info("update match data : " + match.toString());
        logger.info("update result : " + result);

        String url = "";

        if (result == 0) {
            logger.info("게시판 수정 실패");
            model.addAttribute("url", request.getRequestURL());
            model.addAttribute("message", "게시판 수정 실패");
            url = "error/error";
        } else {
            logger.info("게시판 수정 완료");
            url = "redirect:postDetail";
            rattr.addAttribute("match_idx", match.getMatch_idx());
        }
        return url;
    }

    @PostMapping("/postDelete")
    public String postDelete(int match_idx, RedirectAttributes rattr, HttpServletRequest request, Model model) {

        int result = matchService.deleteMatch(match_idx);

        if (result == 0) {
            logger.info("게시판 삭제 실패");
            model.addAttribute("url", request.getRequestURL());
            model.addAttribute("message", "삭제 실패");
            return "error/error";
        } else {
            logger.info("게시판 삭제 성공");
            rattr.addFlashAttribute("result", "deleteSuccess");
            return "redirect:dashboard?tab=matchPost";
        }
    }

    @GetMapping("/sales")
    public ModelAndView businessSales(@RequestParam(required = false) String month,
                                      @RequestParam(required = false) String year,
                                      @RequestParam(required = false) String level,
                                      @RequestParam(required = false) String gender,
                                      ModelAndView modelAndView, HttpSession session) {

        Integer idx = (Integer) session.getAttribute("idx");

        List<Match> list = matchService.getMatchListByIdForSales(idx, month, year, gender, level);

        logger.info("결제 리스트 사이즈 : " + list.size());

        // match_date별로 그룹화
        Map<LocalDate, List<Match>> groupedByDate = list.stream().collect(Collectors.groupingBy(Match::getMatch_date));

        Map<LocalDate, List<Match>> sortedGroupedByDate = groupedByDate.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getKey().compareTo(entry1.getKey())) // 내림차순 정렬
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // 병합 함수
                        LinkedHashMap::new // LinkedHashMap으로 순서 보장
                ));

        Map<LocalDate, Integer> dailyTotalMap = new HashMap<>();

        for (LocalDate date : sortedGroupedByDate.keySet()) {

            List<Match> matchesOnDate = sortedGroupedByDate.get(date);

            for (Match match : matchesOnDate) {
                int playerCount = paymentService.getPlayerCount(match.getMatch_idx());
                match.setPlayerCount(playerCount);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

                String formattedDate = match.getMatch_date().format(formatter);
                match.setFormattedDate(formattedDate);

                String a = match.getMatch_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ' ' + match.getMatch_time();

                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime matchDateTime = LocalDateTime.parse(a, formatter1);

                LocalDateTime currentDateTime = LocalDateTime.now();

                LocalDateTime twoHoursBeforeMatch = matchDateTime.minusHours(2);

                boolean isMatchPast = twoHoursBeforeMatch.isBefore(currentDateTime);
                match.setMatchPast(isMatchPast);
            }

            int dailyTotal = matchesOnDate.stream().mapToInt(match -> match.getPrice() * match.getPlayerCount()).sum();

            dailyTotalMap.put(date, dailyTotal);
        }

        int total = list.stream().filter(Match::isMatchPast).mapToInt(match -> match.getPrice() * match.getPlayerCount()).sum();
        int playerTotal = list.stream().filter(Match::isMatchPast).mapToInt(Match::getPlayerCount).sum();


        modelAndView.setViewName("business/businessSales");
        modelAndView.addObject("groupedByDate", sortedGroupedByDate);
        modelAndView.addObject("list", list.size());
        modelAndView.addObject("dailyTotalMap", dailyTotalMap);
        modelAndView.addObject("total", total);
        modelAndView.addObject("playerTotal", playerTotal);

        return modelAndView;
    }

    @GetMapping("/MatchParticipants")
    public ModelAndView businessMatchParticipants(ModelAndView modelAndView, HttpSession session, @RequestParam(defaultValue = "1") int page) {

        Integer idx = (Integer) session.getAttribute("idx");

        int limit = 10;

        int listCount = matchService.getListCountById(idx);

        List<Match> list = matchService.getMatchListByIdForSales(idx,  null, null, null, null);
        List<Map<String, Object>> results = paymentService.getPaymentListById(idx);

        for (Match match : list) {
            int playerCount = paymentService.getPlayerCount(match.getMatch_idx());
            match.setPlayerCount(playerCount);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

            String formattedDate = match.getMatch_date().format(formatter);
            match.setFormattedDate(formattedDate);

            String a = match.getMatch_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ' ' + match.getMatch_time();

            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime matchDateTime = LocalDateTime.parse(a, formatter1);

            LocalDateTime currentDateTime = LocalDateTime.now();

            LocalDateTime twoHoursBeforeMatch = matchDateTime.minusHours(2);

            boolean isMatchPast = twoHoursBeforeMatch.isBefore(currentDateTime);
            match.setMatchPast(isMatchPast);
        }


        modelAndView.setViewName("business/businessMatchParticipants");
        modelAndView.addObject("matchList", list);
        modelAndView.addObject("list", list.size());
        modelAndView.addObject("results", results);
        modelAndView.addObject("page", page);
        modelAndView.addObject("listcount", listCount);
        modelAndView.addObject("limit", limit);

        return modelAndView;
    }

    @GetMapping("/customerList")
    public ModelAndView businessCustomerList(@RequestParam(required = false) String vip,
                                             @RequestParam(required = false) Integer gender,
                                             @RequestParam(required = false) String age,
                                             ModelAndView modelAndView, HttpSession session) {

        Integer business_idx = (Integer) session.getAttribute("idx");

        List<Map<String, Object>> userList = regularUserService.getUserListForBusiness(business_idx, vip, gender, age);

        for (Map<String, Object> user : userList) {

            String jumin = (String) user.get("jumin");

            int year = Integer.parseInt(jumin.substring(0, 2));
            int month = Integer.parseInt(jumin.substring(2, 4));
            int day = Integer.parseInt(jumin.substring(4, 6));

            int fullYear = (year < 22) ? 2000 + year : 1900 + year;

            LocalDate birthDate = LocalDate.of(fullYear, month, day);
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(birthDate, currentDate);

            user.put("age", period.getYears());

            String status = bcBlacklistService.getStatusById(user.get("idx"), business_idx);
            user.put("status", status);
        }

        modelAndView.setViewName("business/businessCustomerList");
        modelAndView.addObject("userList", userList);
        modelAndView.addObject("list", userList.size());
        modelAndView.addObject("business_idx", business_idx);
        return modelAndView;
    }

    @GetMapping("/blacklist")
    public ModelAndView blacklist(@RequestParam(required = false) String block,
                                  @RequestParam(required = false) String unblock,
                                  HttpSession session, ModelAndView modelAndView){

        Integer business_idx = (Integer) session.getAttribute("idx");

        List<Map<String, Object>> blackList = bcBlacklistService.getBlackListById(business_idx, block, unblock);

        blackList.forEach(item -> {
            item.put("unblocked_at", item.getOrDefault("unblocked_at", null));
        });

        modelAndView.setViewName("business/businessBlackList");
        modelAndView.addObject("blackListSize", blackList.size());
        modelAndView.addObject("blackList", blackList);

        return modelAndView;
    }

    @GetMapping("/inquiry")
    public ModelAndView inquiry(@RequestParam(defaultValue = "1") int page, HttpSession session, ModelAndView modelAndView){

        Integer business_idx = (Integer) session.getAttribute("idx");
        String usertype = (String) session.getAttribute("usertype");

        session.setAttribute("referer", "list");
        int limit = 10;

        int listcount = inquiryService.getListCount(usertype, business_idx);
        List<Inquiry> inquiryList = inquiryService.getInquiryList(page, limit, business_idx, usertype);

        for (Inquiry inquiry : inquiryList) {
            boolean replyExist = inquiryService.replyComplete(inquiry.getInquiry_idx());
            inquiry.setHasReply(replyExist);
        }

        PaginationResult result = new PaginationResult(page, limit, listcount);

        modelAndView.setViewName("business/businessInquiryList");
        modelAndView.addObject("page", page);
        modelAndView.addObject("maxpage", result.getMaxpage());
        modelAndView.addObject("startpage", result.getStartpage());
        modelAndView.addObject("endpage", result.getEndpage());
        modelAndView.addObject("listcount", listcount);
        modelAndView.addObject("inquiryList", inquiryList);
        modelAndView.addObject("limit", limit);

        return modelAndView;
    }

    @GetMapping(value = "/inquiryForm")//board/write
    public String inquiryForm() {
        return "business/businessInquiryForm";
    }

    @PostMapping("/inquiryAdd")
    public String inquiryAdd(Inquiry inquiry) throws Exception {
        MultipartFile uploadfile = inquiry.getUploadfile();

        if (!uploadfile.isEmpty()) {
            String fileDBName = inquiryService.saveUploadedFile(uploadfile, saveFolder);
            inquiry.setInquiry_file(fileDBName);
            inquiry.setOriginal_file(uploadfile.getOriginalFilename());
        }

        inquiryService.insertInquiry(inquiry);
        return "redirect:dashboard?tab=inquiry";
    }

    @GetMapping("/inquiryDetail")
    public ModelAndView inquiryDetail(
            int inquiry_idx, ModelAndView mv, @RequestHeader(value = "referer", required = false) String beforeURL,
            HttpSession session) {

        String sessionReferer = (String) session.getAttribute("referer");
        logger.info("referer: " + beforeURL);

        if(sessionReferer != null && sessionReferer.equals("list")){
            session.removeAttribute("referer");
        }

        Inquiry inquiryData = inquiryService.getDetail(inquiry_idx);
        boolean replyExist = inquiryService.replyComplete(inquiryData.getInquiry_idx()); //답변유무에 따른 수정,삭제 버튼 나타냄/없앰
        inquiryData.setHasReply(replyExist);

        List<InquiryComment> icList = inquiryCommentService.getInquiryCommentList(inquiry_idx);

        if(inquiryData == null){
            logger.info("상세보기 실패");
            mv.setViewName("403");
        }else{
            logger.info("상세보기 성공");
            int icListCount = inquiryCommentService.getListCount(inquiry_idx);
            mv.setViewName("business/businessInquiryDetail");
            mv.addObject("icListCount", icListCount);
            mv.addObject("inquiryData", inquiryData);
            mv.addObject("icList", icList);
        }
        return mv;
    }

    @GetMapping("/inquiryModifyView")
    public ModelAndView businessModifyView(
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
            mv.setViewName("business/businessInquiryModify");
        }
        return mv;
    }


    @PostMapping("/inquiryModifyAction")
    public String businessModifyAction(
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
            url = "redirect:dashboard?tab=inquiry";
            rattr.addAttribute("inquiry_idx", inquiryData.getInquiry_idx());
        }
        return url;
    }

    @GetMapping("/charts")
    public String businessCharts(Model model, HttpSession session,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(required = false) String filter,
                                    @RequestParam(required = false) String gender,
                                    @RequestParam(required = false) String level) {

        Integer business_idx = (Integer) session.getAttribute("idx");

        List<Integer> monthlyData = paymentService.getPlayerCountByMonth(business_idx);
        List<Integer> monthlyMatchData = matchService.getTotalMatchByMonth(business_idx);

        Map<String, Object> playerGenderCount = regularUserService.getPlayerGenderCount(business_idx);

        int playerFemale = (int) playerGenderCount.get("group_2_4");
        int playerMale = (int) playerGenderCount.get("group_1_3");

        int totalPlayers = playerFemale + playerMale;

        double femalePercentage = 0;
        double malePercentage = 0;

        if (totalPlayers > 0) {
            femalePercentage = ((double) playerFemale / totalPlayers) * 100;
            malePercentage = ((double) playerMale / totalPlayers) * 100;
        }

        femalePercentage = Math.round(femalePercentage * 10) / 10.0;
        malePercentage = Math.round(malePercentage * 10) / 10.0;

        int totalMatch = matchService.getTotalMatchById(business_idx);

        session.setAttribute("refer", "list");

        int limit = 20;

        List<Match> list = matchService.getMatchListById(business_idx, filter, gender, level, page, limit);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

        int recruitingMatchCount = 0;
        int completedRecruitmentCount = 0;
        List<Integer> monthlyCompletedRecruitmentCount = new ArrayList<>(Collections.nCopies(12, 0));

        for (Match match : list) {

            int playerCount = paymentService.getPlayerCount(match.getMatch_idx());
            match.setPlayerCount(playerCount);

            String formattedDate = match.getMatch_date().format(formatter);
            match.setFormattedDate(formattedDate);

            String a = match.getMatch_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ' ' + match.getMatch_time();

            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime matchDateTime = LocalDateTime.parse(a, formatter1);

            LocalDateTime currentDateTime = LocalDateTime.now();

            LocalDateTime twoHoursBeforeMatch = matchDateTime.minusHours(2);

            boolean isMatchPast = twoHoursBeforeMatch.isBefore(currentDateTime);
            match.setMatchPast(isMatchPast);

            if (!isMatchPast) {
                recruitingMatchCount++;
            }

            if (isMatchPast && playerCount >= match.getPlayer_min()) {
                completedRecruitmentCount++;

                int month = match.getMatch_date().getMonthValue();
                monthlyCompletedRecruitmentCount.set(month - 1, monthlyCompletedRecruitmentCount.get(month - 1) + 1);
            }
        }

        double recruitmentPercentage = 0;
        double percentage = 0;

        if (totalMatch > 0) {
            recruitmentPercentage = Math.round(((double) completedRecruitmentCount / totalMatch) * 1000.0) / 10.0;
            percentage = 100 - recruitmentPercentage;
        }

        Double avgPrice = matchService.getAvgPrice();
        Double avgPriceByIdx = matchService.getAvgPriceByIdx(business_idx);

        avgPriceByIdx = (avgPriceByIdx == null) ? 0.0 : avgPriceByIdx;

        Long roundedAvgPrice = (long) Math.floor(avgPrice);
        Long roundedAvgPriceByIdx = (long) Math.floor(avgPriceByIdx);

        Long comparison = roundedAvgPriceByIdx - roundedAvgPrice;

        model.addAttribute("monthlyData", monthlyData);
        model.addAttribute("totalMatch", totalMatch);

        model.addAttribute("recruitingMatchCount", recruitingMatchCount);
        model.addAttribute("completedRecruitmentCount", completedRecruitmentCount);

        model.addAttribute("monthlyMatchData", monthlyMatchData);
        model.addAttribute("monthlyCompletedRecruitmentCount", monthlyCompletedRecruitmentCount);

        model.addAttribute("recruitmentPercentage", recruitmentPercentage);
        model.addAttribute("percentage", percentage);

        model.addAttribute("totalPlayers", totalPlayers);
        model.addAttribute("femalePercentage", femalePercentage);
        model.addAttribute("malePercentage", malePercentage);

        model.addAttribute("roundedAvgPrice", roundedAvgPrice);
        model.addAttribute("roundedAvgPriceByIdx", roundedAvgPriceByIdx);

        model.addAttribute("comparison", comparison);

        return "business/businessCharts";
    }

    @GetMapping("/Settings")
    public ModelAndView settings(HttpSession session, ModelAndView modelAndView){

        Integer business_idx = (Integer) session.getAttribute("idx");

        BusinessUser businessUser = businessUserService.getBusinessUserInfoById(business_idx);
        BusinessInfo businessInfo = businessInfoService.getInfoById(business_idx);

        logger.info("businessUser : " + businessUser.toString());

        logger.info("businessInfo : " + businessInfo);

        modelAndView.setViewName("business/businessSettings");
        modelAndView.addObject("businessUser", businessUser);
        modelAndView.addObject("businessInfo", businessInfo);

        return modelAndView;
    }

    @PostMapping("/addInfo")
    public String BusinessInfoAdd(BusinessInfo businessInfo) {

        businessInfoService.insertBusinessInfo(businessInfo);

        return "redirect:/business/dashboard?tab=Settings";
    }

    @PostMapping("/updateInfo")
    public String BusinessInfoUpdate(BusinessInfo businessInfo) {

        businessInfoService.updateBusinessInfo(businessInfo);

        return "redirect:/business/dashboard?tab=Settings";
    }
}
