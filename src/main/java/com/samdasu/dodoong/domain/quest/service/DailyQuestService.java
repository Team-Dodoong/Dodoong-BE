package com.samdasu.dodoong.domain.quest.service;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.quest.dto.request.DailyQuestCreateRequest;
import com.samdasu.dodoong.domain.quest.dto.response.DailyQuestCalendarResponse;
import com.samdasu.dodoong.domain.quest.dto.response.DailyQuestCreateResponse;
import com.samdasu.dodoong.domain.quest.dto.response.DailyQuestListResponse;
import com.samdasu.dodoong.domain.quest.repository.DailyQuestRepository;
import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestSummaryProjection;
import com.samdasu.dodoong.domain.routine.entity.Routine;
import com.samdasu.dodoong.domain.routine.repository.RoutineRepository;
import com.samdasu.dodoong.domain.routine.service.RoutineService;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyQuestService {

    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");

    private final MemberRepository memberRepository;
    private final DailyQuestRepository dailyQuestRepository;
    private final RoutineService routineService;
    private final RoutineRepository routineRepository;

    @Transactional
    public DailyQuestCreateResponse createDailyQuest(Long memberId,
                                                     DailyQuestCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!request.isRoutine()) {
            return createSingleQuest(member, request);
        }
        return createRoutineQuests(member, request);
    }

    public DailyQuestCalendarResponse getCalendar(Long memberId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        LocalDate today = LocalDate.now(ZONE_KST);

        // 오늘까지: 실제 데이터
        Map<LocalDate, DayCountAccumulator> countMap = new TreeMap<>();

        if (!startDate.isAfter(today)) {
            LocalDate realEnd = endDate.isBefore(today) ? endDate : today;
            dailyQuestRepository.countDailyQuestsByPeriod(memberId, startDate, realEnd)
                    .forEach(p -> countMap.put(
                            p.getQuestDate(),
                            new DayCountAccumulator(p.getTotalCount(), p.getCheckedCount())));
        }

        // 미래: 루틴 가상 전개
        LocalDate virtualStart = today.plusDays(1).isAfter(startDate)
                ? today.plusDays(1) : startDate;

        if (!virtualStart.isAfter(endDate)) {
            List<Routine> activeRoutines =
                    routineRepository.findActiveRoutineWithRepeatDays(memberId, virtualStart);

            for (Routine routine : activeRoutines) {
                for (LocalDate date : routine.expandOccurrences(virtualStart, endDate)) {
                    countMap.computeIfAbsent(date, d -> new DayCountAccumulator(0L, 0L))
                            .incrementTotal();;
                }
            }
        }

        List<DailyQuestCalendarResponse.DayCount> days = countMap.entrySet().stream()
                .map(e -> new DailyQuestCalendarResponse.DayCount(
                        e.getKey(),
                        e.getValue().total(),
                        e.getValue().checked()))
                .toList();

        return new DailyQuestCalendarResponse(year, month, days);
    }

    public DailyQuestListResponse getDailyQuestByDate(Long memberId, LocalDate date) {
        List<DailyQuestSummaryProjection> projections =
                dailyQuestRepository.findSummariesByDate(memberId, date);

        return DailyQuestListResponse.of(date, projections);
    }

    // 퀘스트 단일 생성
    private DailyQuestCreateResponse createSingleQuest(
            Member member,
            DailyQuestCreateRequest request
    ) {
        DailyQuest dailyQuest = DailyQuest.create(
                request.questCategory(),
                request.content(),
                LocalDate.now(ZONE_KST),
                member,
                null
        );
        DailyQuest saved = dailyQuestRepository.save(dailyQuest);
        return DailyQuestCreateResponse.ofSingle(saved);
    }

    // 퀘스트 루틴 생성
    private DailyQuestCreateResponse createRoutineQuests(Member member,
                                                         DailyQuestCreateRequest request) {
        LocalDate today = LocalDate.now(ZONE_KST);

        Routine routine = routineService.createRoutine(
                request.questCategory(),
                request.content(),
                request.endDate(),
                request.repeatDays(),
                member
        );

        LocalDate firstQuestDate = routine.findNextQuestDate(today);

        if (firstQuestDate.equals(today)) {
            dailyQuestRepository.save(DailyQuest.create(
                    routine.getQuestCategory(),
                    routine.getContent(),
                    today,
                    member,
                    routine
            ));
        }

        return DailyQuestCreateResponse.ofRoutine(routine, firstQuestDate);
    }

    private static class DayCountAccumulator {
        private long total;
        private final long checked;

        public DayCountAccumulator(long total, long checked) {
            this.total = total;
            this.checked = checked;
        }

        void incrementTotal() {
            total++;
        }

        long total() {
            return total;
        }

        long checked() {
            return checked;
        }
    }
}