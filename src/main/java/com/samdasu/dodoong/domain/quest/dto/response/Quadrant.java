package com.samdasu.dodoong.domain.quest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestQuadrantProjection;

import java.time.LocalDate;
import java.util.List;

public record Quadrant(QuestCategory category, List<QuestItem> quests) {
    public record QuestItem(
            Long dailyQuestId,
            String content,
            LocalDate questDate,
            @JsonProperty("isRoutine") boolean isRoutine,
            Long routineId
    ) {
        public static QuestItem from(DailyQuestQuadrantProjection projection) {
            return new QuestItem(
                    projection.getDailyQuestId(),
                    projection.getContent(),
                    projection.getQuestDate(),
                    projection.getRoutineId() != null,
                    projection.getRoutineId());
        }
    }
}