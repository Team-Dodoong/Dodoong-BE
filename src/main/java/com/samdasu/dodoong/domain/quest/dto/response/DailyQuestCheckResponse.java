package com.samdasu.dodoong.domain.quest.dto.response;

public record DailyQuestCheckResponse(
        DailyQuestSummary quest,
        int experience,
        int experienceDelta
) {
}
