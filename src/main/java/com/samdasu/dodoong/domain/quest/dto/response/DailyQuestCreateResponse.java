package com.samdasu.dodoong.domain.quest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.routine.entity.Routine;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DailyQuestCreateResponse(
        boolean isRoutine,
        Long dailyQuestId,
        Long routineId,
        QuestCategory questCategory,
        String content,
        LocalDate firstQuestDate,
        LocalDate endDate,
        Set<DayOfWeek> repeatDays
) {
    public static DailyQuestCreateResponse ofSingle(DailyQuest dailyQuest) {
        return new DailyQuestCreateResponse(
                false,
                dailyQuest.getId(),
                null,
                dailyQuest.getQuestCategory(),
                dailyQuest.getContent(),
                dailyQuest.getQuestDate(),
                null,
                null
        );
    }

    public static DailyQuestCreateResponse ofRoutine(
            Routine routine,
            LocalDate firstQuestDate
    ) {
        return new DailyQuestCreateResponse(
                true,
                null,
                routine.getId(),
                routine.getQuestCategory(),
                routine.getContent(),
                firstQuestDate,
                routine.getEndDate(),
                Set.copyOf(routine.getRepeatDays())
        );
    }
}
