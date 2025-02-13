package com.Shoots.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class Inquiry {
    @Getter
    @Setter
    private MultipartFile uploadfile;
    private int inquiry_idx;
    private String inquiry_type;
    private int inquiry_ref_idx;
    private String title;
    private String content;
    private String inquiry_file;
    private String register_date;
    private int cnt;
    private String user_id;
    private String business_id;
    private int idx;
    private int commentCount;
    private boolean hasReply;
    private String resolved_id;
    private String original_file;

}
