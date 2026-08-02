package com.samdasu.dodoong.domain.routine.repository;

import com.samdasu.dodoong.domain.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    @Query("""
    SELECT r FROM Routine r
    JOIN FETCH r.member
    WHERE r.endDate >= :today AND :dayOfWeek MEMBER OF r.repeatDays""")
    List<Routine> findActiveRoutinesForDay(LocalDate today, DayOfWeek dayOfWeek);

    @Query("""
    SELECT DISTINCT r FROM Routine r
    JOIN FETCH r.repeatDays
    WHERE r.member.id = :memberId AND r.endDate >= :date
    """)
    List<Routine> findActiveRoutineWithRepeatDays(@Param("memberId") Long memberId,
                                                  @Param("date") LocalDate date);

    Optional<Routine> findByIdAndMemberId(Long id, Long memberId);

    // 회원 탈퇴 시 회원의 루틴 전체 삭제
    void deleteAllByMemberId(Long memberId);
}