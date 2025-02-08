package com.Shoots.mybatis.mapper;

import com.Shoots.domain.PostComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/*
Mapper 인터페이스란 MyBatis에서 SQL 쿼리를 매핑하고 실행하기 위해 사용하는 인터페이스입니다.
MyBatis-Spring은 Mapper 인터페이스를 이용해서 실제 SQL 처리가 되는 클래스를 자동으로 생성합니다.
*/
@Mapper
public interface PostCommentMapper {


    public int getListCount(int post_idx);

    public List<PostComment> getCommentList(Map<String, Integer> map);

    public int commentsInsert(PostComment c);

    public int commentsReply(PostComment c);

    public int commentsDelete(int comment_idx);

    public int commentsUpdate(PostComment co);


    List<PostComment> getMyCommentList(int id);

    int getMyCommentListCount(int id);

    //comment 차단
    void setBlock(int id);
}
