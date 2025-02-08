package com.Shoots.service;

import com.Shoots.domain.chat_room_log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface chat_service {
    public int create_chat_room();
    public void insert_chat_log(Map<String,Object> map);
    public List<chat_room_log> get_chat_log(Map<String, Object> map);
    public void join_chat_room(List<Integer> user_idx_list, int chat_room_idx, int match_idx);
    Integer get_match_chat_room_idx(int match_idx);
}
