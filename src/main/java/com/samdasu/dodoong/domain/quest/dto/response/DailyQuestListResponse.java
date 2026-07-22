package com.samdasu.dodoong.domain.quest.dto.response;

import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestSummaryProjection;

import java.time.LocalDate;
import java.util.List;

public record DailyQuestListResponse(
        LocalDate date,
        List<DailyQuestSummary> quests
) {
    public static DailyQuestListResponse of(LocalDate date,
                                            List<DailyQuestSummaryProjection> projections) {
        List<DailyQuestSummary> quests = projections.stream()
                .map(DailyQuestSummary::from)
                .toList();
        return new DailyQuestListResponse(date, quests);
    }
}
