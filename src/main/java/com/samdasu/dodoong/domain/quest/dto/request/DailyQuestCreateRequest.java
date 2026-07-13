package com.samdasu.dodoong.domain.quest.dto.request;

import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import jakarta.validation.constraints.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public record DailyQuestCreateRequest(
        @NotNull(message = "카테고리는 필수입니다.")
        QuestCategory questCategory,
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 100, message = "내용은 100자 이하여야 합니다.")
        String content,
        boolean isRoutine,
        Set<DayOfWeek> repeatDays,
        @FutureOrPresent(message = "마감일은 오늘 이후여야 합니다.")
        LocalDate endDate
) {
    @AssertTrue(message = "반복 설정 시 반복 요일을 하나 이상 선택해야 합니다.")
    public boolean isRepeatDaysValid() {
        return !isRoutine || (repeatDays != null && !repeatDays.isEmpty());
    }

    @AssertTrue(message = "반복 퀘스트는 마감일을 입력해야 합니다.")
    public boolean isEndDateRequired() {
        return !isRoutine || endDate != null;
    }

    @AssertTrue(message = "단일 퀘스트에는 반복 설정을 입력할 수 없습니다.")
    public boolean isSingleQuestValid() {
        return isRoutine || (
                (repeatDays == null || repeatDays.isEmpty()) && endDate == null);
    }
}
