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
        @Size(max = 100)
        String content,
        @NotNull
        @FutureOrPresent(message = "퀘스트 날짜는 오늘 이후여야 합니다.")
        LocalDate questDate,
        boolean isRoutine,
        Set<DayOfWeek> repeatDays
) {
    @AssertTrue(message = "반복 설정 시 반복 요일을 하나 이상 선택해야 합니다.")
    public boolean isRepeatDaysValid() {
        return !isRoutine || (repeatDays != null && !repeatDays.isEmpty());
    }
}
