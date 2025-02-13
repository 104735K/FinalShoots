package com.Shoots.mybatis.mapper;

import com.Shoots.domain.chat_room;
import com.Shoots.domain.chat_room_log;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface chat_mapper {
    void make_chat_room(chat_room chat_room);
    void insert_chat_log(Map<String, Object> map);
    List<chat_room_log> get_chat_log(Map<String, Object> map);
    void join_user(int chat_room_idx, int user_idx, int match_idx);
    Integer get_match_chat_room_idx(int match_idx);
}
