package com.Shoots.mybatis.mapper;

import com.Shoots.domain.Notice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NoticeMapper {
    public int getSearchListCount(Map<String, Object> map); //글의 갯수
    public List<Notice> getNoticeList(Map<String, Object> map); //글 리스트 가져오기
    public void setReadCountUpdate(int id); //조회수 업데이트
    public Notice getDetail(int id); //상세내역 조회
    public void insertNotice(Notice notice); //공지사항 등록하기
    public int updateNotice(Notice notice); //공지사항 수정
    public void deleteNotice(int id); //공지사항 삭제
}
