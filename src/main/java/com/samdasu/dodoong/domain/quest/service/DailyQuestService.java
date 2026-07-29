package com.samdasu.dodoong.domain.quest.service;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.quest.dto.request.DailyQuestCheckRequest;
import com.samdasu.dodoong.domain.quest.dto.request.DailyQuestCreateRequest;
import com.samdasu.dodoong.domain.quest.dto.request.DailyQuestUpdateRequest;
import com.samdasu.dodoong.domain.quest.dto.response.*;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.quest.repository.DailyQuestRepository;
import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestQuadrantProjection;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyQuestService {

    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");
    private static final int EXPERIENCE_PER_QUEST = 10;

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

            dailyQuestRepository.countNoRoutineQuestsByPeriod(memberId, virtualStart, endDate)
                    .forEach(p -> countMap
                            .computeIfAbsent(p.getQuestDate(), k -> new DayCountAccumulator(0L, 0L))
                            .addTotal(p.getTotalCount()));
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
        LocalDate today = LocalDate.now(ZONE_KST);

        // 오늘까지: 실제 데이터
        if (!date.isAfter(today)) {
            List<DailyQuestSummaryProjection> projections =
                    dailyQuestRepository.findSummariesByDate(memberId, date);
            return DailyQuestListResponse.of(date, projections);
        }

        // 미래: 가상 전개
        List<DailyQuestSummary> realQuests =
                dailyQuestRepository.findSummariesByDateAndNoRoutine(memberId, date).stream()
                        .map(DailyQuestSummary::from)
                        .toList();

        List<Routine> activeRoutines =
                routineRepository.findActiveRoutineWithRepeatDays(memberId, date);

        List<DailyQuestSummary> virtualQuests = activeRoutines.stream()
                .filter(r -> r.getRepeatDays().contains(date.getDayOfWeek()))
                .filter(r -> !date.isAfter(r.getEndDate()))
                .map(DailyQuestSummary::virtualFrom)
                .toList();

        List<DailyQuestSummary> merged =
                Stream.concat(realQuests.stream(), virtualQuests.stream()).toList();

        return new DailyQuestListResponse(date, merged);
    }

    public DailyQuestQuadrantResponse getQuadrant(Long memberId) {
        List<DailyQuestQuadrantProjection> projections =
                dailyQuestRepository.findIncompleteQuests(memberId);

        Map<QuestCategory, List<Quadrant.QuestItem>> grouped = projections.stream()
                .collect(Collectors.groupingBy(
                        DailyQuestQuadrantProjection::getQuestCategory,
                        () -> new EnumMap<>(QuestCategory.class),
                        Collectors.mapping(Quadrant.QuestItem::from, Collectors.toList())));

        for (QuestCategory category : QuestCategory.values()) {
            grouped.putIfAbsent(category, List.of());
        }
        return DailyQuestQuadrantResponse.of(grouped);
    }

    public Quadrant getQuadrantDetail(Long memberId, QuestCategory questCategory) {
        List<Quadrant.QuestItem> quests = dailyQuestRepository.
                findIncompleteQuestsByCategory(memberId, questCategory).stream()
                .map(Quadrant.QuestItem::from)
                .toList();
        return new Quadrant(questCategory, quests);
    }

    @Transactional
    public DailyQuestSummary updateDailyQuest(Long memberId,
                                              Long dailyQuestId,
                                              DailyQuestUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DailyQuest dailyQuest = dailyQuestRepository.findByIdAndMemberId(dailyQuestId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.DAILY_QUEST_NOT_FOUND));

        dailyQuest.updateQuest(request.questCategory(), request.content());

        return DailyQuestSummary.from(dailyQuest);
    }

    @Transactional
    public DailyQuestCheckResponse checkDailyQuest(Long memberId,
                                                   Long dailyQuestId,
                                                   DailyQuestCheckRequest request) {
        Member member = memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DailyQuest dailyQuest = dailyQuestRepository.findByIdAndMemberId(dailyQuestId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.DAILY_QUEST_NOT_FOUND));

        boolean target = request.isChecked();
        int before = member.getExperience();

        if (dailyQuest.changeChecked(target)) {
            if (target) {
                member.addExperience(EXPERIENCE_PER_QUEST);
            } else {
                member.subtractExperience(EXPERIENCE_PER_QUEST);
            }
        }

        int after = member.getExperience();
        return DailyQuestCheckResponse.of(dailyQuest, after, after - before);
    }

    @Transactional
    public DailyQuestPostponeResponse postpone(Long memberId, Long dailyQuestId) {
        DailyQuest dailyQuest = dailyQuestRepository
                .findByIdAndMemberId(dailyQuestId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.DAILY_QUEST_NOT_FOUND));

        dailyQuest.postponeToNextDay();

        return DailyQuestPostponeResponse.from(dailyQuest);
    }

    @Transactional
    public void deleteDailyQuest(Long memberId, Long dailyQuestId) {
        DailyQuest dailyQuest = dailyQuestRepository
                .findByIdAndMemberId(dailyQuestId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.DAILY_QUEST_NOT_FOUND));

        dailyQuest.checkDeletable();

        dailyQuestRepository.delete(dailyQuest);
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

        void addTotal(long delta) {
            this.total += delta;
        }

        long total() {
            return total;
        }

        long checked() {
            return checked;
        }
    }
}