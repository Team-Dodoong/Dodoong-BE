package com.samdasu.dodoong.domain.quest.repository.projection;

import com.samdasu.dodoong.domain.quest.entity.QuestCategory;

public interface DailyQuestSummaryProjection {
    Long getDailyQuestId();
    QuestCategory getQuestCategory();
    String getContent();
    boolean getIsChecked();
    Long getRoutineId();
}
