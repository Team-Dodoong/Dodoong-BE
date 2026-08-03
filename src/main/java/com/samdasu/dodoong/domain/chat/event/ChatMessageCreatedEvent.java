package com.samdasu.dodoong.domain.chat.event;

import com.samdasu.dodoong.domain.chat.dto.response.ChatMessageResponse;

public record ChatMessageCreatedEvent(
        Long partyId,
        ChatMessageResponse message
) {
}
