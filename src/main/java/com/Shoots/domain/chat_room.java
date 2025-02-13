package com.Shoots.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class chat_room {
    private int chat_room_idx;
    private LocalDateTime created_at;
}
