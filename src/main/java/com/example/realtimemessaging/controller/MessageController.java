package com.example.realtimemessaging.controller;


import com.example.realtimemessaging.modal.Message;
import com.example.realtimemessaging.service.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MessageController {

    private final Sender sender;
    private final SimpMessageSendingOperations messagingTemplate; //Của websocket
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    public MessageController(Sender sender, SimpMessageSendingOperations messagingTemplate) {
        this.sender = sender;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send-message")
    public void sendMessage(@Payload Message chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        chatMessage.setSessionId(headerAccessor.getSessionId());
        sender.send("messaging", chatMessage); //gửi message lên topic "messaging" của kafka
        logger.info("Sending message to /topic/public: " + chatMessage);
        messagingTemplate.convertAndSend("/topic/public", chatMessage); //gửi message đến topic public của websocket
        logger.info("Message sent to /topic/public: " + chatMessage);
    }

    @MessageMapping("/chat.add-user")
    @SendTo("/topic/public") //Gửi đến broker websocket
    public Message addUser(
            @Payload Message chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        }

        return chatMessage;
    }
}