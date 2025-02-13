package com.Shoots.mybatis.mapper;

import com.Shoots.domain.Report;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    public int insertReport(Report report);
    public List<Report> selectReportedUsers(String reporter);
    public Report selectCheckReportDuplicate(String reporter,int post_idx, int comment_idx,String category);
    public int selectReportedCount(String reported,String category);
    public List<Map<String, Object>> getReportList(Map<String, Object> map); //report list 가져오기
    public int getReportCount(); //총 신고 개수
}
