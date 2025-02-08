package com.Shoots.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Report {
    private int reportIdx;
    private String reporter;
    private String reportedUser;
    private String category;
    private String content;
    private String detail;
    @JsonProperty
    private int PostIdx;
    @JsonProperty
    private int CommentIdx;
}
