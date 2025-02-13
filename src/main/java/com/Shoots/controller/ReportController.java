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

    ReportService reportService;
    private static Logger logger = (Logger) LoggerFactory.getLogger(ReportController.class);

    @PostMapping("/insertReport")
    @ResponseBody
    public Map<String, Object> insertReport(Model model, HttpSession session, @RequestBody Report report) {

        Map<String, Object> resp = new HashMap<>();
        String reporter = String.valueOf(session.getAttribute("id"))    ;

        if(session.getAttribute("id") == null) {
            resp.put("msg", "로그인 후 이용해주세요.");
            return resp;
        }

        report.setReporter((String) session.getAttribute("id"));
        logger.info("insertReport 정보 : "+String.valueOf(report));

        if(reportService.selectCheckReportDuplicate(report.getReporter(), report.getPost_idx(), report.getComment_idx(), report.getCategory()) != null)
            resp.put("msg", "이미 접수된 신고입니다.");
        else if(reportService.insertReport(report) == 1){
            resp.put("msg",  "신고가 접수되었습니다. " + " [신고 내용]: " + report.getReported_content());
            //resp.put("reportCnt", reportService.selectReportedCount(report.getReportedUser(), report.getCategory()));
        }
        else
            resp.put("msg", "신고에 실패했습니다.");
        return resp;
    }

}
