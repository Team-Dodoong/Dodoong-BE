package com.samdasu.dodoong.domain.streak.service.achievement;

import java.time.LocalDate;
import java.util.Set;

public interface QuestAchievementSource {

    long countCompletedQuests(Long memberId, LocalDate targetDate);

    Set<Long> findAchievedMemberIds(LocalDate targetDate);
}
