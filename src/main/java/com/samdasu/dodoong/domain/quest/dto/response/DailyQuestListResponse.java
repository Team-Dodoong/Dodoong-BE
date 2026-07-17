package com.samdasu.dodoong.domain.quest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestSummaryProjection;
import com.samdasu.dodoong.domain.routine.entity.Routine;

import java.time.LocalDate;
import java.util.List;

public record DailyQuestListResponse(
        LocalDate date,
        List<QuestSummary> quests
) {
    public record QuestSummary(
            Long dailyQuestId,
            QuestCategory questCategory,
            String content,
            @JsonProperty("isChecked") boolean isChecked,
            @JsonProperty("isRoutine") boolean isRoutine,
            Long routineId
    ) {
        public static QuestSummary from(DailyQuestSummaryProjection projection) {
            return new QuestSummary(
                    projection.getDailyQuestId(),
                    projection.getQuestCategory(),
                    projection.getContent(),
                    projection.getIsChecked(),
                    projection.getRoutineId() != null,
                    projection.getRoutineId()
            );
        }

        public static QuestSummary virtualFrom(Routine routine) {
            return new QuestSummary(
                    null,
                    routine.getQuestCategory(),
                    routine.getContent(),
                    false,
                    true,
                    routine.getId()
            );
        }
    }

    public static DailyQuestListResponse of(LocalDate date,
                                            List<DailyQuestSummaryProjection> projections) {
        List<QuestSummary> quests = projections.stream()
                .map(QuestSummary::from)
                .toList();
        return new DailyQuestListResponse(date, quests);
    }
}
