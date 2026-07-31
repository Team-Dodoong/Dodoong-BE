package com.samdasu.dodoong.domain.chat.dto.response;

import com.samdasu.dodoong.domain.chat.entity.ChatMessage;
import com.samdasu.dodoong.domain.member.entity.Member;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long messageId,
        Long partyId,
        Long senderId,
        String senderNickname,
        String senderProfileImageUrl,
        String content,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage message) {
        Member sender = message.getSender();

        return new ChatMessageResponse(
                message.getId(),
                message.getParty().getId(),
                sender.getId(),
                sender.getNickname(),
                sender.getProfileImageUrl(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
