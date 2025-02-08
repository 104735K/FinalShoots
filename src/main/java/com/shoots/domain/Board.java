package com.shoots.domain;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Board {
    private int board_idx;
    private int readcount;
    private String writer;
    private String title;
    private String content;
    private String register_date;
}
