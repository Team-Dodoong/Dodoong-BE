package com.samdasu.dodoong.domain.routine.repository;

import com.samdasu.dodoong.domain.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
}