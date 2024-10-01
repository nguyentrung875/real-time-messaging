package com.example.realtimemessaging.service;

import com.example.realtimemessaging.modal.Message;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Receiver {

//    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
    private final SimpMessageSendingOperations messagingTemplate;
    private final SimpUserRegistry userRegistry;//Object chứa thông tin các user đang kết nối Websocket và các session của họ

    public Receiver(SimpMessageSendingOperations messagingTemplate, SimpUserRegistry userRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.userRegistry = userRegistry;
    }

    @KafkaListener(topics = "messaging", groupId = "chat")
    public void consume(Message chatMessage) {
        log.info("Received message from Kafka: " + chatMessage);

        //gửi message đến topic public của websocket, có thể thay thế bằng @SendTo("topic/public")
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
        log.info("Message sent to /topic/public: " + chatMessage);


//        for (SimpUser user : userRegistry.getUsers()) {
//            for (SimpSession session : user.getSessions()) {
//                //Loại bỏ phiên người dùng hiện tại khỏi việc nhận tin nhắn (tránh gửi lại tin nhắn cho chính người gửi).
//                if (!session.getId().equals(chatMessage.getSessionId())) {
//                    messagingTemplate.convertAndSendToUser(session.getId(), "/topic/public", chatMessage);
//                }
//            }
//        }
    }
}
