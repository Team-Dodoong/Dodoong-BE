package com.samdasu.dodoong.domain.quest.repository;

import com.samdasu.dodoong.domain.quest.dto.response.DailyQuestQuadrantResponse;
import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestCountProjection;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestQuadrantProjection;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestSummaryProjection;
import com.samdasu.dodoong.domain.quest.repository.projection.MonthlyPartyParticipationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface DailyQuestRepository extends JpaRepository<DailyQuest, Long> {

    @Query("""
    SELECT DISTINCT dq.routine.id FROM DailyQuest dq
    WHERE dq.questDate = :questDate AND dq.routine.id IN :routineIds""")
    Set<Long> findExistingRoutineIds(@Param("questDate") LocalDate questDate,
                                     @Param("routineIds") Collection<Long> routineIds);

    // 월 캘린더용 날짜별 집계
    @Query("""
    SELECT dq.questDate AS questDate,
        COUNT(dq) AS totalCount,
        SUM(CASE WHEN dq.isChecked = true THEN 1L ELSE 0L END) AS checkedCount
    FROM DailyQuest dq
    WHERE dq.member.id = :memberId
        AND dq.questDate BETWEEN :startDate AND :endDate
    GROUP BY dq.questDate
    ORDER BY dq.questDate ASC
    """)
    List<DailyQuestCountProjection> countDailyQuestsByPeriod(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 특정 날짜 퀘스트 목록
    @Query("""
    SELECT dq.id AS dailyQuestId,
        dq.questCategory AS questCategory,
        dq.content AS content,
        dq.isChecked AS isChecked,
        r.id AS routineId
    FROM DailyQuest dq
    LEFT JOIN dq.routine r
    WHERE dq.member.id = :memberId
        AND dq.questDate = :questDate
    ORDER BY dq.id ASC
    """)
    List<DailyQuestSummaryProjection> findSummariesByDate(
            @Param("memberId") Long memberId,
            @Param("questDate") LocalDate questDate
    );

    @Query("""
    SELECT CASE WHEN COUNT(dq) > 0 THEN true ELSE false END
    FROM DailyQuest dq
    WHERE dq.member.id = :memberId
        AND dq.content = :content
        AND dq.isChecked = true
        AND dq.questDate = :questDate
    """)
    boolean existsCheckedPartyQuest(
            @Param("memberId") Long memberId,
            @Param("content") String content,
            @Param("questDate") LocalDate questDate
    );

    @Query("""
    SELECT dq.member.id AS memberId,
        COUNT(DISTINCT dq.questDate) AS participationCount
    FROM DailyQuest dq
    WHERE dq.member.id IN :memberIds
        AND dq.content = :content
        AND dq.isChecked = true
        AND dq.questDate BETWEEN :startDate AND :endDate
    GROUP BY dq.member.id
    """)
    List<MonthlyPartyParticipationProjection> countMonthlyPartyParticipations(
            @Param("memberIds") List<Long> memberIds,
            @Param("content") String content,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT dq.id AS dailyQuestId,
        dq.questCategory AS questCategory,
        dq.content AS content,
        dq.questDate AS questDate,
        r.id AS routineId
    FROM DailyQuest dq
    LEFT JOIN dq.routine r
    WHERE dq.member.id = :memberId
        AND dq.isChecked = false
    ORDER BY dq.questDate ASC, dq.id ASC
    """)
    List<DailyQuestQuadrantProjection> findIncompleteQuests(
            @Param("memberId") Long memberId
    );

    @Query("""
    SELECT dq.id AS dailyQuestId, dq.questCategory AS questCategory,
        dq.content AS content, dq.questDate AS questDate, r.id AS routineId
    FROM DailyQuest dq
    LEFT JOIN dq.routine r
    WHERE dq.member.id = :memberId AND dq.questCategory = :questCategory
        AND dq.isChecked = false 
    ORDER BY dq.questDate ASC, dq.id ASC
    """)
    List<DailyQuestQuadrantProjection> findIncompleteQuestsByCategory(
            @Param("memberId") Long memberId,
            @Param("questCategory") QuestCategory questCategory
    );
}
