package com.Shoots.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class chat_room_log {
    private int chat_room_idx;
    private String content;
    private String sender;
    private String sent_at;
}
