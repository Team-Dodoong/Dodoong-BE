package com.samdasu.dodoong.domain.chat.service;

import com.samdasu.dodoong.domain.chat.dto.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private static final String PARTY_TOPIC_PREFIX = "/topic/parties/";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastToParty(Long partyId, ChatMessageResponse message) {
        messagingTemplate.convertAndSend(PARTY_TOPIC_PREFIX + partyId, message);
    }
}
