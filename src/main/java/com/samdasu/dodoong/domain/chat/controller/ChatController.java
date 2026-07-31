package com.samdasu.dodoong.domain.chat.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.chat.dto.response.ChatHistoryResponse;
import com.samdasu.dodoong.domain.chat.service.ChatService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/parties/{partyId}/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public BaseResponse<ChatHistoryResponse> getChatHistory(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long partyId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size) {
        ChatHistoryResponse response =
                chatService.getChatHistory(partyId, principal.memberId(), cursor, size);
        return BaseResponse.ok(response);
    }
}
