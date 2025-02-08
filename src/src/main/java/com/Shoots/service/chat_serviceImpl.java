package com.Shoots.service;

import com.Shoots.domain.chat_room;
import com.Shoots.domain.chat_room_log;
import com.Shoots.mybatis.mapper.chat_mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class chat_serviceImpl implements chat_service {

    private chat_mapper dao;

    @Override
    public int create_chat_room() {
        chat_room chatRoom = new chat_room();
        dao.make_chat_room(chatRoom); // 채팅방 생성. chatRoom domain에 매핑
        return chatRoom.getChat_room_idx(); 
    }

    public void join_chat_room(List<Integer> user_idx_list, int chat_room_idx, int match_idx){
        for (int user_idx : user_idx_list)
            dao.join_user(chat_room_idx, user_idx, match_idx);
    }

    @Override
    public Integer get_match_chat_room_idx(int match_idx) {
        return dao.get_match_chat_room_idx(match_idx);
    }

    @Override
    public void insert_chat_log(Map<String, Object> map) {
        dao.insert_chat_log(map);
    }

    @Override
    public List<chat_room_log> get_chat_log(Map<String, Object> map) {
        return dao.get_chat_log(map);
    }

}
