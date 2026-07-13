package com.samdasu.dodoong.domain.quest.scheduler;

import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.repository.DailyQuestRepository;
import com.samdasu.dodoong.domain.routine.entity.Routine;
import com.samdasu.dodoong.domain.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyQuestScheduler {
    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");

    private final RoutineRepository routineRepository;
    private final DailyQuestRepository dailyQuestRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void generateTodayQuests() {
        LocalDate today = LocalDate.now(ZONE_KST);

        List<Routine> todayRoutines = routineRepository.findAllByEndDateGreaterThanEqual(today).stream()
                .filter(r -> r.getRepeatDays().contains(today.getDayOfWeek()))
                .toList();

        if (todayRoutines.isEmpty()) {
            log.info("[DailyQuestScheduler] {}에 실행할 routine이 없습니다.", today);
            return;
        }

        List<Long> routineIds = todayRoutines.stream().map(Routine::getId).toList();
        Set<Long> alreadyCreated = dailyQuestRepository.findExistingRoutineIds(today, routineIds);

        List<DailyQuest> toCreate = todayRoutines.stream()
                .filter(r -> !alreadyCreated.contains(r.getId()))
                .map(r -> DailyQuest.create(
                        r.getQuestCategory(),
                        r.getContent(),
                        today,
                        r.getMember(),
                        r
                ))
                .toList();

        if (toCreate.isEmpty()) {
            log.info("[DailyQuestScheduler] {}의 routines {}개가 모두 이미 생성되었습니다.", today, todayRoutines.size());
            return;
        }

        dailyQuestRepository.saveAll(toCreate);
        log.info("[DailyQuestScheduler] {}의 일일 퀘스트 {}개를 생성했습니다. (전체 루틴={}, 생성 제외={})",
                today, toCreate.size(), todayRoutines.size(), alreadyCreated.size());
    }
}
