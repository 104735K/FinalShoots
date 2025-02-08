package com.Shoots.controller;

import com.Shoots.domain.PostComment;
import com.Shoots.service.PostCommentService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RestController
@RequestMapping(value="/comment")
public class PostCommentController {

    private static final Logger logger = LoggerFactory.getLogger(PostCommentController.class);

    private PostCommentService postCommentService;

    public PostCommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }


    @PostMapping(value="/list")
    public Map<String, Object> CommentList(@RequestParam("post_idx") int post_idx,
                                           @RequestParam("state") int state,
//                                           @RequestParam("user_id") String user_id,
                                           HttpSession session) {
        List<PostComment> list = postCommentService.getCommentList(post_idx, state, session);
//        Iterator<PostComment> iterator = list.iterator();
//        while(iterator.hasNext()){
//            PostComment postComment = iterator.next();
//            if(user_id.equals(postComment.getUser_id())) //
//                postComment.setSecret(true);
//        }

        int listcount = postCommentService.getListCount(post_idx);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("commentlist", list);
        map.put("listcount", listcount);
        map.put("state", state);
        logger.info("/comment/list");
        return map;
    }




    @PostMapping(value = "/add")
    public int commentAdd(PostComment co, HttpSession session) {
//        String user_id = (String) session.getAttribute("user_id");
//        co.setUser_id(user_id);
        logger.info(String.valueOf(co));
        return postCommentService.commentsInsert(co);
    }

    @PostMapping(value = "/reply")
    public int commentReply(PostComment co) {
        return postCommentService.commentsReply(co);
    }

    @PostMapping(value = "/update")
    public int commentUpdate(PostComment co) {
        return postCommentService.commentsUpdate(co);
    }

    @PostMapping(value = "/delete")
    public int commentDelete(int comment_idx) {
        return postCommentService.commentsDelete(comment_idx);
    }



}
