package com.samdasu.dodoong.domain.quest.repository.projection;

import java.time.LocalDate;

public interface DailyQuestCountProjection {
    LocalDate getQuestDate();
    Long getTotalCount();
    Long getCheckedCount();
}
