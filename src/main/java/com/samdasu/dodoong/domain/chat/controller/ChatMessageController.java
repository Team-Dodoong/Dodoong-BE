package com.samdasu.dodoong.domain.chat.controller;

import com.samdasu.dodoong.domain.chat.dto.request.ChatMessageRequest;
import com.samdasu.dodoong.domain.chat.service.ChatService;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import com.samdasu.dodoong.global.websocket.WebSocketPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    @MessageExceptionHandler(CustomException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public BaseResponse<Void> handleCustomException(CustomException e) {
        return BaseResponse.of(e.getBaseCode());
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public BaseResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        return BaseResponse.of(ErrorCode.INVALID_FIELD_ERROR);
    }
}
