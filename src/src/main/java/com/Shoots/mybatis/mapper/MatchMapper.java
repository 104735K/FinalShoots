package com.Shoots.mybatis.mapper;

import com.Shoots.domain.Match;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface MatchMapper {
    public int getListCount();

    List<Match> getMatchList(HashMap<String, Object> map);

    void insertMatch(Match match);

    Match getDetail(int matchIdx);

    int updateMatch(Match match);

    int deleteMatch(int matchIdx);

    List<Match> getMatchListById(HashMap<String, Object> map);

    int getListCountById(Integer idx);

    List<Match> getMatchListByIdForSales(HashMap<String, Object> map);

    List<Match> getMatchListByMatchTime(LocalDate matchDate, LocalTime matchTime);

    List<Match> getMatchListByToday();

    List<Match> getMatchListByDeadline(LocalDateTime deadline, int i);

    int getTotalMatchById(Integer business_idx);

    List<Map<String, Object>> getTotalMatchByMonth(Integer business_idx);

    Double getAvgPrice();

    Double getAvgPriceByIdx(Integer business_idx);

    List<Match> getMatchListByIdForParticipants(HashMap<String, Object> map);
}
