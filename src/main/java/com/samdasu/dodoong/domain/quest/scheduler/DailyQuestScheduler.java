package com.samdasu.dodoong.domain.quest.scheduler;

import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.repository.DailyQuestRepository;
import com.samdasu.dodoong.domain.routine.entity.Routine;
import com.samdasu.dodoong.domain.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class DailyQuestScheduler {
    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");

    private final RoutineRepository routineRepository;
    private final DailyQuestRepository dailyQuestRepository;
    private final TransactionTemplate transactionTemplate;

    public DailyQuestScheduler(RoutineRepository routineRepository,
                               DailyQuestRepository dailyQuestRepository,
                               PlatformTransactionManager transactionManager) {
        this.routineRepository = routineRepository;
        this.dailyQuestRepository = dailyQuestRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void generateTodayQuests() {
        LocalDate today = LocalDate.now(ZONE_KST);

        List<Routine> todayRoutines = routineRepository.findActiveRoutinesForDay(today, today.getDayOfWeek());

        if (todayRoutines.isEmpty()) {
            log.info("[DailyQuestScheduler] {}에 실행할 routine이 없습니다.", today);
            return;
        }

        List<Long> routineIds = todayRoutines.stream().map(Routine::getId).toList();
        Set<Long> alreadyCreated = dailyQuestRepository.findExistingRoutineIds(today, routineIds);

        int created = 0;
        int skippedPreCheck = 0;
        int skippedRace = 0;

        for (Routine routine : todayRoutines) {
            if (alreadyCreated.contains(routine.getId())) {
                skippedPreCheck++;
                continue;
            }
            try {
                transactionTemplate.executeWithoutResult(status ->
                        dailyQuestRepository.save(DailyQuest.create(
                                routine.getQuestCategory(),
                                routine.getContent(),
                                today,
                                routine.getMember(),
                                routine)));
                created++;
            } catch (DataIntegrityViolationException e) {
                skippedRace++;
                log.debug("[DailyQuestScheduler] 중복 스킵: routineId={}, date={}", routine.getId(), today);
            }
        }
        log.info("[DailyQuestScheduler] {} → created={}, skipped(pre-check)={}, skipped(race)={}",
                today, created, skippedPreCheck, skippedRace);
    }
}
