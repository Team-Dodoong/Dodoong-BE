package com.samdasu.dodoong.domain.routine.repository;

import com.samdasu.dodoong.domain.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findAllByEndDateGreaterThanEqual(LocalDate date);
}