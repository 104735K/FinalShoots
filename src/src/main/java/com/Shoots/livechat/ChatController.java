package com.Shoots.livechat;

import com.Shoots.domain.chat_room_log;
import com.Shoots.service.RegularUserService;
import com.Shoots.service.chat_service;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping(value="/livechat")
public class ChatController {

    private static Logger logger = LoggerFactory.getLogger(ChatController.class);
    chat_service chatService;
    RegularUserService regularUserService;

    @GetMapping(value = "/livechat")
    public String livechat(HttpSession session, Model model,  @RequestParam("match_idx") int match_idx) {
        model.addAttribute("id", session.getId());

        int chat_room_idx = chatService.get_match_chat_room_idx(match_idx) == null ? 0 : chatService.get_match_chat_room_idx(match_idx);

        //match_idx로 개설된 chat room이 없으면 새로 만들고 가입시킴
        if(chat_room_idx == 0){
            List<Integer> user_idx_list = new ArrayList<>();
            user_idx_list.add((int)session.getAttribute("idx"));

            chat_room_idx = chatService.create_chat_room();
            chatService.join_chat_room(user_idx_list, chat_room_idx, match_idx);
            model.addAttribute("chatRoomNumber", chat_room_idx);
        }
        //있으면 match_idx로 개설된 chat room number
        else
            model.addAttribute("chatRoomNumber", chatService.get_match_chat_room_idx(match_idx));
        return "livechat/livechat";
    }

    @ResponseBody
    @PostMapping(value = "/join_chat_room")
    public Map<String, Object> join_chat_room(@RequestBody chat_room_log room_log) {
        logger.info(room_log.toString());

        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("chat_room_idx", room_log.getChat_room_idx());
        map.put("content", room_log.getContent());
        map.put("sender", room_log.getSender());
        chatService.insert_chat_log(map);

        resp.put("status", "success");
        return resp;
    }

    @ResponseBody
    @PostMapping(value = "/makelog")
    public Map<String, Object> makelog(@RequestBody chat_room_log room_log) {
        logger.info(room_log.toString());

        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("chat_room_idx", room_log.getChat_room_idx());
        map.put("content", room_log.getContent());
        map.put("sender", room_log.getSender());
        chatService.insert_chat_log(map);

        resp.put("status", "success");
        return resp;
    }

    @ResponseBody
    @PostMapping(value = "/getlog")
    public List<chat_room_log> getlog(@RequestBody chat_room_log room_log) {
        logger.info(room_log.toString());

        Map<String, Object> map = new HashMap<>();
        map.put("chat_room_idx", room_log.getChat_room_idx());

        return chatService.get_chat_log(map);
    }

    @MessageMapping("/chat{topicName}")
    @SendTo("/topic/{topicName}")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage,
                                   @DestinationVariable String topicName) {
        return chatMessage;
    }

    @MessageMapping("{topicName}")
    @SendTo("/topic/{topicName}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                                SimpMessageHeaderAccessor headerAccessor,
                               @DestinationVariable String topicName) {
        // Add username in web socket session
        logger.info("topicName : " +  topicName);
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }



}
