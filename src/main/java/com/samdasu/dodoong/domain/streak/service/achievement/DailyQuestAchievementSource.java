package com.samdasu.dodoong.domain.streak.service.achievement;

import com.samdasu.dodoong.domain.quest.repository.DailyQuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DailyQuestAchievementSource implements QuestAchievementSource {

    private final DailyQuestRepository dailyQuestRepository;

    @Override
    public long countCompletedQuests(Long memberId, LocalDate targetDate) {
        return dailyQuestRepository.countByMemberIdAndQuestDateAndIsCheckedTrue(memberId, targetDate);
    }

    @Override
    public Set<Long> findAchievedMemberIds(LocalDate targetDate) {
        return dailyQuestRepository.findMemberIdsWithCheckedQuest(targetDate);
    }
}
