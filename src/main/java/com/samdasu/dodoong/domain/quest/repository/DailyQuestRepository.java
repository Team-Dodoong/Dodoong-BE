package com.samdasu.dodoong.domain.quest.repository;

import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestCountProjection;
import com.samdasu.dodoong.domain.quest.repository.projection.DailyQuestSummaryProjection;
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
}
