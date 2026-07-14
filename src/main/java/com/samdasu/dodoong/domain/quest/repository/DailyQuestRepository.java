package com.samdasu.dodoong.domain.quest.repository;

import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

public interface DailyQuestRepository extends JpaRepository<DailyQuest, Long> {

    @Query("SELECT DISTINCT dq.routine.id FROM DailyQuest dq " +
            "WHERE dq.questDate = :questDate AND dq.routine.id IN :routineIds")
    Set<Long> findExistingRoutineIds(@Param("questDate") LocalDate questDate,
                                     @Param("routineIds") Collection<Long> routineIds);
}
