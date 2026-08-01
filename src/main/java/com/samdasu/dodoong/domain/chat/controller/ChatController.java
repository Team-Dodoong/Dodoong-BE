package com.samdasu.dodoong.domain.chat.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.chat.dto.response.ChatHistoryResponse;
import com.samdasu.dodoong.domain.chat.dto.response.ChatRoomResponse;
import com.samdasu.dodoong.domain.chat.service.ChatService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/parties/{partyId}/chats")
    public BaseResponse<ChatHistoryResponse> getChatHistory(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long partyId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size) {
        ChatHistoryResponse response =
                chatService.getChatHistory(partyId, principal.memberId(), cursor, size);
        return BaseResponse.ok(response);
    }

    @GetMapping("/chat-rooms")
    public BaseResponse<List<ChatRoomResponse>> getMyChatRooms(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        List<ChatRoomResponse> response = chatService.getMyChatRooms(principal.memberId());
        return BaseResponse.ok(response);
    }
}
