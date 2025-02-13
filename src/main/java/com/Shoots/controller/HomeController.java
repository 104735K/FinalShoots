package com.Shoots.controller;

import com.Shoots.domain.BusinessUser;
import com.Shoots.domain.Match;
import com.Shoots.domain.RegularUser;
import com.Shoots.domain.Weather;
import com.Shoots.redis.RedisService;
import com.Shoots.service.MatchService;
import com.Shoots.service.PaymentService;
import com.Shoots.service.weather.WeatherService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class HomeController {
    private static Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final RedisService redisService;
    private WeatherService weatherService;
    private MatchService matchService;
    private PaymentService paymentService;

    @GetMapping("/mainBefore") //로그인이 성공하면 main 주소로 가기 전 로그인 유저 타입을 확인하는 경로
    public void home(@AuthenticationPrincipal Object principal, HttpSession session, HttpServletResponse response) throws IOException {

        if (principal instanceof RegularUser) {
            RegularUser regularUser = (RegularUser) principal;
        } else if (principal instanceof BusinessUser) {
            BusinessUser businessUser = (BusinessUser) principal;
        }

        if ("pending".equals(session.getAttribute("businessAccess"))) {
            response.setContentType("text/html; charset=utf-8");
            response.setCharacterEncoding("utf-8");

            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>");
            out.println("if(confirm('미승인 상태입니다. 관리자의 승인을 기다려주세요.')){");
            out.println("location.href='/Shoots/logout';");
            out.println("} else { location.href='/Shoots/logout'; }");
            out.println("</script>");
            out.flush();
        }
        response.sendRedirect("/Shoots/main");

    }

    @GetMapping(value = "/main")
    public String main(Model model, @RequestParam(defaultValue = "서울특별시") String first,
                       @RequestParam(defaultValue = "강남구") String second,
                       @RequestParam(defaultValue = "역삼1동") String third) throws IOException, ParseException {

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        System.out.println("first: " + first + ", second: " + second + ", third: " + third);

        Map<String, Integer> weather = redisService.getLocationData(first, second, third);
        System.out.println("weather = x : " + weather.get("nx") + " / y : " + weather.get("ny"));

        Weather weatherData = weatherService.getWeather(today, weather.get("nx"), weather.get("ny"));
        List<Weather> weatherDataForecast = weatherService.getWeatherForecast(today, weather.get("nx"), weather.get("ny"));

        List<Weather> firstSixWeatherData = weatherDataForecast.stream()
                .limit(6)
                .map(forecast -> {
                    forecast.setWindSpeedAsNumber(Double.parseDouble(forecast.getWindSpeed()));
                    return forecast;
                })
                .collect(Collectors.toList());

        int currentTime = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")));
        System.out.println("Current Time = " + currentTime);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusHours = now.plusHours(2);

        LocalDate matchDate = nowPlusHours.toLocalDate();
        LocalTime matchTime = nowPlusHours.toLocalTime().withSecond(0).withNano(0);

        LocalDateTime deadline = matchTime.minusHours(2).atDate(matchDate);

        List<Match> list = matchService.getMatchListByDeadline(deadline, 3);

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

            logger.info("현재시간 기준 마감여부 (true-마감 / false-신청가능) isMatchPast : " + match.getMatch_idx() + " = " + match.isMatchPast());

        }

        model.addAttribute("weather", weatherData);
        model.addAttribute("firstSixWeatherData", firstSixWeatherData);
        model.addAttribute("today", today);
        model.addAttribute("currentTime", currentTime);
        model.addAttribute("first", first);
        model.addAttribute("second", second);
        model.addAttribute("third", third);
        model.addAttribute("matchList", list);

        return "home/home";
    }

    @GetMapping("/getOptions")
    @ResponseBody
    public Map<String, Map<String, List<String>>> getLocationOptions() throws IOException {
        Map<String, Map<String, List<String>>> options = redisService.getLocationOptions();
        return options;
    }

    @GetMapping("/error/403")
    public String error_403() {
        return "error/403";
    }



}
