package com.samdasu.dodoong.domain.quest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestSummaryProjection;
import com.samdasu.dodoong.domain.routine.entity.Routine;

public record DailyQuestSummary(
        Long dailyQuestId,
        QuestCategory questCategory,
        String content,
        @JsonProperty("isChecked") boolean isChecked,
        @JsonProperty("isRoutine") boolean isRoutine,
        Long routineId
) {
    public static DailyQuestSummary from(DailyQuest dailyQuest) {
        Routine routine = dailyQuest.getRoutine();
        Long routineId = (routine != null) ? routine.getId() : null;

        return new DailyQuestSummary(
                dailyQuest.getId(),
                dailyQuest.getQuestCategory(),
                dailyQuest.getContent(),
                dailyQuest.isChecked(),
                routineId != null,
                routineId
        );
    }

    public static DailyQuestSummary from(DailyQuestSummaryProjection projection) {
        return new DailyQuestSummary(
                projection.getDailyQuestId(),
                projection.getQuestCategory(),
                projection.getContent(),
                projection.getIsChecked(),
                projection.getRoutineId() != null,
                projection.getRoutineId()
        );
    }

    public static DailyQuestSummary virtualFrom(Routine routine) {
        return new DailyQuestSummary(
                null,
                routine.getQuestCategory(),
                routine.getContent(),
                false,
                true,
                routine.getId()
        );
    }
}