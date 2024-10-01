package com.example.realtimemessaging.service;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import com.example.realtimemessaging.modal.Message;
import com.example.realtimemessaging.modal.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener //lắng nghe sự kiện có client mới kết nối thành công websocket
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener //lắng nghe sự kiện ngắt kết nối với websocket
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        //Lấy thông tin user từ session attributes đã lưu trước đó
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            logger.info("User Disconnected: " + username);

            //Gửi thông điệp DISCONNECT tới tất cả ng dùng trong kênh "/topic/public"
            Message chatMessage = new Message();
            chatMessage.setType(MessageType.DISCONNECT);
            chatMessage.setSender(username);
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}