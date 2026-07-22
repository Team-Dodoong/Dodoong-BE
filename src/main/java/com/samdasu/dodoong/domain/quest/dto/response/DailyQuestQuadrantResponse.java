package com.samdasu.dodoong.domain.quest.dto.response;

import com.samdasu.dodoong.domain.quest.entity.QuestCategory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public record DailyQuestQuadrantResponse(
        List<Quadrant> quadrants
) {
    public static DailyQuestQuadrantResponse of(Map<QuestCategory, List<Quadrant.QuestItem>> grouped) {
        List<Quadrant> quadrants = Arrays.stream(QuestCategory.values())
                .map(category -> new Quadrant(
                        category,
                        grouped.getOrDefault(category, List.of())))
                .toList();
        return new DailyQuestQuadrantResponse(quadrants);
    }
}
