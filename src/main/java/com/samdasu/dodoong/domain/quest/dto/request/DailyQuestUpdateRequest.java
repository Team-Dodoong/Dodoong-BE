package com.samdasu.dodoong.domain.quest.dto.request;

import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public record DailyQuestUpdateRequest(
        QuestCategory questCategory,
        @Size(max = 100, message = "내용은 100자 이하여야 합니다.")
        String content
) {
        @AssertTrue(message = "내용은 공백일 수 없습니다.")
        public boolean isContentValid() {
                return content == null || !content.isBlank();
        }

        @AssertTrue(message = "수정할 항목을 하나 이상 입력해야 합니다.")
        public boolean isAnyFieldPresent() {
                return questCategory != null || content != null;
        }
}
