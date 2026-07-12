package com.samdasu.dodoong.domain.quest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.routine.entity.Routine;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DailyQuestCreateResponse(
        Long dailyQuestId,
        Long routineId,
        QuestCategory questCategory,
        String content,
        LocalDate questDate,
        boolean isChecked,
        Set<DayOfWeek> repeatDays
) {
    public static DailyQuestCreateResponse of(DailyQuest dailyQuest, Routine routine) {
        return new DailyQuestCreateResponse(
                dailyQuest.getId(),
                routine != null ? routine.getId() : null,
                dailyQuest.getQuestCategory(),
                dailyQuest.getContent(),
                dailyQuest.getQuestDate(),
                dailyQuest.isChecked(),
                routine != null ? routine.getRepeatDays() : null
        );
    }
}
