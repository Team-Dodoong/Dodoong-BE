package com.samdasu.dodoong.domain.quest.dto.response;

import com.samdasu.dodoong.domain.quest.entity.DailyQuest;

public record DailyQuestCheckResponse(
        DailyQuestSummary quest,
        int level,
        int experience,
        int experienceDelta,
        boolean leveledUp
) {
    public static DailyQuestCheckResponse of(DailyQuest quest,
                                             int level,
                                             int experience,
                                             int experienceDelta,
                                             boolean leveledUp) {
        return new DailyQuestCheckResponse(
                DailyQuestSummary.from(quest),
                level,
                experience,
                experienceDelta,
                leveledUp
        );
    }
}
