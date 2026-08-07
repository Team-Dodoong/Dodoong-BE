package com.samdasu.dodoong.domain.chat.dto.response;

import com.samdasu.dodoong.domain.chat.entity.ChatMessage;
import com.samdasu.dodoong.domain.party.entity.Party;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        Long partyId,
        String partyImageUrl,
        String partyName,
        int memberCount,
        boolean isPublic,
        String lastMessage,
        LocalDateTime lastMessageAt
) {
    public static ChatRoomResponse of(Party party,
                                      String partyImageUrl,
                                      int memberCount,
                                      ChatMessage lastMessage) {
        return new ChatRoomResponse(
                party.getId(),
                partyImageUrl,
                party.getName(),
                memberCount,
                party.isPublic(),
                lastMessage != null ? lastMessage.getContent() : null,
                lastMessage != null ? lastMessage.getCreatedAt() : null
        );
    }
}
