package com.samdasu.dodoong.domain.streak.scheduler;

import com.samdasu.dodoong.domain.streak.service.StreakService;
import com.samdasu.dodoong.domain.streak.service.achievement.QuestAchievementSource;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StreakScheduler {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    private final StreakService streakService;
    private final List<QuestAchievementSource> achievementSources;

    @Scheduled(cron = "30 0 0 * * *", zone = "Asia/Seoul")
    public void evaluateYesterdayStreaks() {
        LocalDate targetDate = LocalDate.now(KOREA_ZONE).minusDays(1);

        Set<Long> memberIds = findAchievedMemberIds(targetDate);

        for (Long memberId : memberIds) {
            streakService.evaluateDailyStreak(memberId, targetDate);
        }
    }

    private Set<Long> findAchievedMemberIds(LocalDate targetDate) {
        Set<Long> memberIds = new HashSet<>();

        for (QuestAchievementSource source : achievementSources) {
            memberIds.addAll(source.findAchievedMemberIds(targetDate));
        }

        return memberIds;
    }
}