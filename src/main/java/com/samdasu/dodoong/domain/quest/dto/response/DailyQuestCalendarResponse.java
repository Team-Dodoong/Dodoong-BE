package com.samdasu.dodoong.domain.quest.dto.response;

import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestCountProjection;

import java.time.LocalDate;
import java.util.List;

public record DailyQuestCalendarResponse(
        int year,
        int month,
        List<DayCount> days
) {
    public record DayCount(
            LocalDate date,
            long totalCount,
            long checkedCount
    ) {
        public static DayCount from(DailyQuestCountProjection projection) {
            return new DayCount(
                    projection.getQuestDate(),
                    projection.getTotalCount(),
                    projection.getCheckedCount()
            );
        }
    }
    public static DailyQuestCalendarResponse of(int year,
                                                int month,
                                                List<DailyQuestCountProjection> projections) {
        List<DayCount> days = projections.stream()
                .map(DayCount::from)
                .toList();
        return new DailyQuestCalendarResponse(year, month, days);
    }
}
