package com.Shoots.service;

import com.Shoots.domain.Report;
import com.Shoots.mybatis.mapper.ReportMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private ReportMapper dao;

    @Override
    public int insertReport(Report report) {
        return dao.insertReport(report);
    }

    @Override
    public List<Report> selectReportedUsers(String reporter) {
        return dao.selectReportedUsers(reporter);
    }

    @Override
    public Report selectCheckReportDuplicate(String reporter, int post_idx, int comment_idx, String category) {
        return dao.selectCheckReportDuplicate(reporter, post_idx, comment_idx, category);
    }

    @Override
    public int selectReportedCount(String reported, String category) {
        return dao.selectReportedCount(reported, category);
    }

    @Override
    public List<Map<String, Object>> getReportList(int page, int limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        int offset = (page - 1) * limit;
        map.put("offset", offset);
        int pageSize = limit;
        map.put("pageSize", pageSize);
        return dao.getReportList(map);
    }

    @Override
    public int getReportCount() {
        return dao.getReportCount();
    }
}
