package com.samdasu.dodoong.domain.routine.repository;

import com.samdasu.dodoong.domain.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    @Query("""
    SELECT r FROM Routine r
    JOIN FETCH r.member
    WHERE r.endDate >= :today AND :dayOfWeek MEMBER OF r.repeatDays""")
    List<Routine> findActiveRoutinesForDay(LocalDate today, DayOfWeek dayOfWeek);
}