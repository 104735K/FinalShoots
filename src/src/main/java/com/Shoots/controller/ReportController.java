package com.Shoots.controller;

import com.Shoots.domain.Report;
import com.Shoots.service.ReportService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
public class ReportController {

//    BoardService boardService;
    ReportService reportService;
    private static Logger logger = (Logger) LoggerFactory.getLogger(ReportController.class);
//    @GetMapping("/report")
//    public String location(Model model, HttpSession session) {
//
//        //신고대상 제거 로직
//        List<Board> boardList = boardService.selectBoardList();
//        List<Report> reportList = reportService.selectReportedUsers(String.valueOf(session.getAttribute("id"))); // 신고자 == userId 인 report정보 가져옴
//        Iterator<Board> iterator = boardList.iterator();
//        while (iterator.hasNext() && reportList.size() > 0) {
//            Board board = iterator.next();
//            for(Report report : reportList) {
//                if(board.getWriter().equals(report.getReportedUser())){
//                    iterator.remove();
//                    break;
//                }
//            }
//        }
//
//        model.addAttribute("boardList", boardList);
//        logger.info("session 값 : " + session.getAttribute("id"));
//        return "report/reportList";
//    }

    @PostMapping("/insertReport")
    @ResponseBody
    public Map<String, Object> insertReport(Model model, HttpSession session, @RequestBody Report report) {

        Map<String, Object> resp = new HashMap<>();
        String reporter = String.valueOf(session.getAttribute("id"))    ;

        if(report.getCategory().equals("COMMENT")){
            //logger.info("CommentIdx 타입: " + report.getCommentIdx().getClass().getSimpleName());
            logger.info(" Post idx  : " + report.getPostIdx());
            logger.info(" comment idx  : " + report.getCommentIdx());
        }
        if(session.getAttribute("id") == null) {
            resp.put("msg", "로그인 후 이용해주세요.");
            return resp;
        }

        report.setReporter((String) session.getAttribute("id"));
        logger.info("insertReport 정보 : "+String.valueOf(report));

        if(reportService.selectCheckReportDuplicate(report.getReporter(), report.getPostIdx(), report.getCommentIdx(), report.getCategory()) != null)
            resp.put("msg", "이미 접수된 신고입니다.");
        else if(reportService.insertReport(report) == 1){
            //getReportedUser >> 이름은 변경 안했지만 댓글 내용 값 가져오게 변경함
            resp.put("msg",  "신고가 접수되었습니다. " + " [신고 댓글]: " + report.getReportedUser());
            //resp.put("reportCnt", reportService.selectReportedCount(report.getReportedUser(), report.getCategory()));
        }
        else
            resp.put("msg", "신고에 실패했습니다.");
        return resp;
    }

}
