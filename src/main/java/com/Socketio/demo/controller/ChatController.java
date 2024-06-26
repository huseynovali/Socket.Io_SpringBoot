package com.Socketio.demo.controller;

import com.Socketio.demo.dto.request.ChatMessageRequest;
import com.Socketio.demo.dto.response.ChatMessageResponse;
import com.Socketio.demo.dto.response.ChatRoomResponse;
import com.Socketio.demo.model.ChatMessage;
import com.Socketio.demo.model.ChatNotification;
import com.Socketio.demo.service.ChatMessageService;
import com.Socketio.demo.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {


    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageRequest chatMessage) {
        System.out.println("hello");
        ChatMessage savedMsg = chatMessageService.saveChatMessage(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId().toString(), "/queue/messages",
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getMessage()
                )
        );
    }

    @GetMapping("api/v1/use/messages/{roomId}")
    public ResponseEntity<List<ChatMessageResponse>> findChatMessages(@PathVariable("roomId") Long RoomId) {
        return ResponseEntity.ok(chatMessageService.getChatMessageByRoomId(RoomId));
    }



}