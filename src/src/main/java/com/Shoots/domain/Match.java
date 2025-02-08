package com.Shoots.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class Match {
    private int match_idx;
    private int writer;
    private LocalDate match_date;
    private LocalTime match_time;
    private int player_max;
    private int player_min;
    private String player_gender;
    private String match_level;
    private String team_style;
    private int price;
    private LocalDateTime register_date;

    private String formattedDate;
    private String formattedTime;

    private int playerCount;
    private String business_name;
    private String address;

    private boolean matchPast;


    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }
}
