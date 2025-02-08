package com.Shoots.service;

import com.Shoots.domain.PostComment;
import com.Shoots.mybatis.mapper.PostCommentMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostCommentServiceImpl implements PostCommentService {

    private PostCommentMapper dao;

    public PostCommentServiceImpl(PostCommentMapper dao) {

        this.dao = dao;
    }

    @Override
    public int getListCount(int post_idx) {
        return dao.getListCount(post_idx);
    }



    @Override
    public List<PostComment> getCommentList(int post_idx, int state, HttpSession session) {

        int startrow = 1;
        int endrow=state*3;

        // 페이징 처리용 매핑
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("post_idx", post_idx);
        map.put("state", state);
        map.put("start", startrow);
        map.put("end", endrow);
        return dao.getCommentList(map);


    }



    @Override
    public int commentsInsert(PostComment c) {
        return dao.commentsInsert(c);
    }

    @Override
    public int commentsReply(PostComment c) {
        return dao.commentsReply(c);
    }

    @Override
    public List<PostComment> getMyCommentList(int id) {
        return dao.getMyCommentList(id);
    }

    @Override
    public int getMyCommentListCount(int id) {
        return dao.getMyCommentListCount(id);
    }

    @Override
    public void setBlock(int id) {
        dao.setBlock(id);
    }


    @Override
    public int commentsUpdate(PostComment co) {
        return dao.commentsUpdate(co);
    }

    @Override
    public int commentsDelete(int num) {
        return dao.commentsDelete(num);
    }



}
