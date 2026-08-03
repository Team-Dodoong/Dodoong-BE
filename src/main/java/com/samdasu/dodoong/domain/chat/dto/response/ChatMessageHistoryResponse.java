package com.samdasu.dodoong.domain.chat.dto.response;

import com.samdasu.dodoong.domain.chat.entity.ChatMessage;
import com.samdasu.dodoong.domain.member.entity.Member;

import java.time.LocalDateTime;

public record ChatMessageHistoryResponse(
        Long messageId,
        Long senderId,
        String senderNickname,
        String senderProfileImageKey,
        String content,
        boolean isMine,
        LocalDateTime createAt
) {
    public static ChatMessageHistoryResponse of(ChatMessage message, Long viewerId) {
        Member sender = message.getSender();
        return new ChatMessageHistoryResponse(
                message.getId(),
                sender.getId(),
                sender.getNickname(),
                sender.getProfileImageKey(),
                message.getContent(),
                sender.getId().equals(viewerId), // 내 메시지 여부 판단
                message.getCreatedAt()
        );
    }
}
