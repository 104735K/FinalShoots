package com.Shoots.domain;

import java.util.List;

public class PaginationResult {
    private int maxpage;    // 총 페이지 수
    private int startpage;  // 시작 페이지
    private int endpage;    // 끝 페이지
    private List<Post> postlist; // 게시글 목록 추가

    // Getter and Setter methods
    public int getMaxpage() {
        return maxpage;
    }

    public void setMaxpage(int maxpage) {
        this.maxpage = maxpage;
    }

    public int getStartpage() {
        return startpage;
    }

    public void setStartpage(int startpage) {
        this.startpage = startpage;
    }

    public int getEndpage() {
        return endpage;
    }

    public void setEndpage(int endpage) {
        this.endpage = endpage;
    }

    public List<Post> getPostlist() {
        return postlist;
    }

    public void setPostlist(List<Post> postlist) {
        this.postlist = postlist;
    }

    // PaginationResult 생성자
    public PaginationResult(int page, int limit, int listCount) {
        // 총 페이지 수 계산
        this.maxpage = (listCount + limit - 1) / limit;

        // 시작 페이지 계산 (1, 11, 21, 31 ...)
        this.startpage = ((page - 1) / 10) * 10 + 1;

        // 끝 페이지 계산 (10, 20, 30, 40 ...)
        this.endpage = startpage + 10 - 1;

        // 끝 페이지가 총 페이지 수를 초과하면 끝 페이지를 총 페이지 수로 설정
        if (endpage > maxpage) {
            this.endpage = maxpage;
        }
    }
}
