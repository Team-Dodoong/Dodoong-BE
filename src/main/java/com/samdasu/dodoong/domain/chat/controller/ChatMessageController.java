package com.samdasu.dodoong.domain.chat.controller;

import com.samdasu.dodoong.domain.chat.dto.request.ChatMessageRequest;
import com.samdasu.dodoong.domain.chat.service.ChatService;
import com.samdasu.dodoong.global.websocket.WebSocketPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatService chatService;

    @MessageMapping("/parties/{partyId}/chat") // "/app/parties/{partyId}/chat"
    public void sendMessage(
            @DestinationVariable Long partyId,
            @Valid @Payload ChatMessageRequest request,
            Principal principal) {
        Long senderId = ((WebSocketPrincipal) principal).memberId();
        chatService.sendMessage(partyId, senderId, request);
    }
}
