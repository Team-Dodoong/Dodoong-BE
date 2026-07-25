package com.samdasu.dodoong.domain.streak.service;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.streak.entity.Streak;
import com.samdasu.dodoong.domain.streak.repository.StreakRepository;
import com.samdasu.dodoong.domain.streak.service.achievement.QuestAchievementSource;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StreakService {

    // 일일퀘스트 또는 파티 퀘스트를 합하여 1개 이상 완료 시 스트릭 인정    /*
    private static final long REQUIRED_COMPLETED_QUEST_COUNT = 1L;

    private final StreakRepository streakRepository;
    private final MemberRepository memberRepository;
    private final List<QuestAchievementSource> achievementSources;

    @Transactional
    public void evaluateDailyStreak(Long memberId, LocalDate targetDate) {

        //완료한 퀘스트 합산
        //TODO: 파티 퀘스트 구현체 추가해야 파티 퀘스트까지 합산 됨
        long completedQuestCount = achievementSources.stream()
                .mapToLong(source -> source.countCompletedQuests(memberId, targetDate))
                .sum();

        if (completedQuestCount < REQUIRED_COMPLETED_QUEST_COUNT) {
            return;
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Streak latestStreak = streakRepository.findTopByMemberIdOrderByLastCheckedDateDesc(memberId)
                .orElse(null);

        if (latestStreak == null) {
            createNewStreak(member, targetDate);
            return;
        }

        if (latestStreak.isAlreadyReflected(targetDate)) {
            return;
        }

        if (latestStreak.canContinue(targetDate)) {
            latestStreak.continueStreak(targetDate);
            return;
        }

        createNewStreak(member, targetDate);
    }

    private void createNewStreak(Member member, LocalDate targetDate) {
        Streak streak = Streak.start(member, targetDate);

        streakRepository.save(streak);
    }
}