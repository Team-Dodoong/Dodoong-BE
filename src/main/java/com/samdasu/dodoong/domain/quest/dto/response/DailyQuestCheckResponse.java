package com.samdasu.dodoong.domain.quest.dto.response;

import com.samdasu.dodoong.domain.quest.entity.DailyQuest;

public record DailyQuestCheckResponse(
        DailyQuestSummary quest,
        int experience,
        int experienceDelta
) {
    public static DailyQuestCheckResponse of(DailyQuest quest,
                                             int experience,
                                             int experienceDelta) {
        return new DailyQuestCheckResponse(
                DailyQuestSummary.from(quest),
                experience,
                experienceDelta
        );
    }
}
