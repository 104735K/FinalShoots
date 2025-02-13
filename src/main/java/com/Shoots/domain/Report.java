package com.Shoots.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Report {
    private int report_idx;
    private String reporter;
    private String reported_content;
    private String category;
    private String content;
    private String detail;
    @JsonProperty
    private int post_idx;
    @JsonProperty
    private int comment_idx;
}
