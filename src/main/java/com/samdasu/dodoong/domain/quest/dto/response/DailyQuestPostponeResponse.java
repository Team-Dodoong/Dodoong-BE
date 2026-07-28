package com.samdasu.dodoong.domain.quest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;

import java.time.LocalDate;

public record DailyQuestPostponeResponse(
        Long dailyQuestId,
        QuestCategory questCategory,
        String content,
        LocalDate questDate,
        @JsonProperty("isChecked") boolean isChecked
) {
    public static DailyQuestPostponeResponse from(DailyQuest dailyQuest) {
        return new DailyQuestPostponeResponse(
                dailyQuest.getId(),
                dailyQuest.getQuestCategory(),
                dailyQuest.getContent(),
                dailyQuest.getQuestDate(),
                dailyQuest.isChecked()
        );
    }
}
