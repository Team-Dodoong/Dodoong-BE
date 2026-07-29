package com.samdasu.dodoong.domain.quest.repository.projection;

import com.samdasu.dodoong.domain.quest.entity.QuestCategory;

import java.time.LocalDate;

public interface DailyQuestQuadrantProjection {
    Long getDailyQuestId();
    QuestCategory getQuestCategory();
    String getContent();
    LocalDate getQuestDate();
    Long getRoutineId();
}
