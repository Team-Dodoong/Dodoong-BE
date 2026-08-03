package com.samdasu.dodoong.domain.chat.dto.response;

import java.util.List;

public record ChatHistoryResponse(
        List<ChatMessageHistoryResponse> messages,
        boolean hasNext,
        Long nextCursor
) {
}
