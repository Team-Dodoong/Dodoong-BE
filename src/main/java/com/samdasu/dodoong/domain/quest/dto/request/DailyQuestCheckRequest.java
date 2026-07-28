package com.samdasu.dodoong.domain.quest.dto.request;

import jakarta.validation.constraints.NotNull;

public record DailyQuestCheckRequest(
        @NotNull(message = "체크 상태는 필수입니다.")
        Boolean isChecked
) {
}
