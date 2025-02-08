package com.Shoots.service;

import com.Shoots.domain.Match;
import com.Shoots.mybatis.mapper.MatchMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MatchServiceImpl implements MatchService{

    private MatchMapper dao;

    public MatchServiceImpl(MatchMapper dao) {
        this.dao = dao;
    }

    @Override
    public void insertMatch(Match match) {
        dao.insertMatch(match);
    }

    @Override
    public List<Match> getMatchList(String filter, String gender, String level, int page, int limit, String business_idx) {

        HashMap<String, Object> map = new HashMap<String, Object>();

        int offset = (page - 1) * limit;

        map.put("filter", (filter != null && !filter.isEmpty()) ? filter : null);
        map.put("gender", gender);
        map.put("level", level);
        map.put("business_idx", business_idx);

        map.put("limit", limit);
        map.put("offset", offset);


        return dao.getMatchList(map);
    }

    @Override
    public int getListCount() {
        return dao.getListCount();
    }

    @Override
    public Match getDetail(int matchIdx) {
        return dao.getDetail(matchIdx);
    }

    @Override
    public int updateMatch(Match match) {
        return dao.updateMatch(match);
    }

    @Override
    public int deleteMatch(int matchIdx) {
        return dao.deleteMatch(matchIdx);
    }

    @Override
    public List<Match> getMatchListById(Integer idx, String filter, String gender, String level, int page, int limit) {

        int offset = (page - 1) * limit;

        HashMap<String, Object> map = new HashMap<>();

        map.put("idx", idx);

        map.put("filter", (filter != null && !filter.isEmpty()) ? filter : null);
        map.put("gender", gender);
        map.put("level", level);

        map.put("limit", limit);
        map.put("offset", offset);


        return dao.getMatchListById(map);
    }

    @Override
    public int getListCountById(Integer idx) {
        return dao.getListCountById(idx);
    }

    @Override
    public List<Match> getMatchListByIdForSales(Integer idx, String month, String year, String gender, String level) {

        HashMap<String, Object> map = new HashMap<>();

        map.put("idx", idx);
        map.put("month", month);
        map.put("year", year);
        map.put("gender", gender);
        map.put("level", level);

        return dao.getMatchListByIdForSales(map);
    }

    @Override
    public List<Match> getMatchListByMatchTime(LocalDate matchDate, LocalTime matchTime) {
        return dao.getMatchListByMatchTime(matchDate, matchTime);
    }

    @Override
    public List<Match> getMatchListByDeadline(LocalDateTime deadline, int i) {
        return dao.getMatchListByDeadline(deadline, i);
    }

    @Override
    public int getTotalMatchById(Integer business_idx) {
        return dao.getTotalMatchById(business_idx);
    }

    @Override
    public List<Integer> getTotalMatchByMonth(Integer business_idx) {
        List<Map<String, Object>> results = dao.getTotalMatchByMonth(business_idx);
        List<Integer> monthlyData = new ArrayList<>(Collections.nCopies(12, 0));

        for (Map<String, Object> row : results) {
            Integer month = (Integer) row.get("month");
            Integer matchCount = ((Number) row.get("match_count")).intValue();
            monthlyData.set(month - 1, matchCount);
        }
        return monthlyData;
    }

    @Override
    public Double getAvgPrice() {
        return dao.getAvgPrice();
    }

    @Override
    public Double getAvgPriceByIdx(Integer business_idx) {
        return dao.getAvgPriceByIdx(business_idx);
    }


}
