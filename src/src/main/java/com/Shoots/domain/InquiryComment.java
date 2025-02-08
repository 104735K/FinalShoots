package com.Shoots.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryComment {
    private int i_comment_idx;
    private int inquiry_idx;
    private int writer;
    private String content;
    private String register_date;
    private String user_id;
    private String business_id;
}
