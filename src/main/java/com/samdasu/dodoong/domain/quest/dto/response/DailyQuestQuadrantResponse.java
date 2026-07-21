package com.samdasu.dodoong.domain.quest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestQuadrantProjection;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public record DailyQuestQuadrantResponse(
        List<Quadrant> quadrants
) {
    public record Quadrant(QuestCategory category, List<QuestItem> quests) {}

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

    public static DailyQuestQuadrantResponse of(Map<QuestCategory, List<QuestItem>> grouped) {
        List<Quadrant> quadrants = Arrays.stream(QuestCategory.values())
                .map(category -> new Quadrant(
                        category,
                        grouped.getOrDefault(category, List.of())))
                .toList();
        return new DailyQuestQuadrantResponse(quadrants);
    }
}
