package com.samdasu.dodoong.domain.chat.event;

import com.samdasu.dodoong.domain.chat.service.ChatMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatMessagePublishListener {

    private final ChatMessagePublisher chatMessagePublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChatMessageCreated(ChatMessageCreatedEvent event) {
        chatMessagePublisher.broadcastToParty(event.partyId(), event.message());
    }
}
