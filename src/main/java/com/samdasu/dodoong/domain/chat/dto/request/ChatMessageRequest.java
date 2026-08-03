package com.samdasu.dodoong.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @NotBlank(message = "메시지 내용은 필수입니다.")
        @Size(max = 500, message = "메시지는 500자 이내여야 합니다.")
        String content
) {
}
