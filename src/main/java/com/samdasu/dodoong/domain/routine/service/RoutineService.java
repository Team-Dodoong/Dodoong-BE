package com.samdasu.dodoong.domain.routine.service;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.routine.entity.Routine;
import com.samdasu.dodoong.domain.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineService {

    private final RoutineRepository routineRepository;

    @Transactional
    public Routine createRoutine(QuestCategory questCategory,
                                 String content,
                                 LocalDate endDate,
                                 Set<DayOfWeek> repeatDays,
                                 Member member) {
        Routine routine = Routine.create(questCategory, content, endDate, repeatDays, member);
        return routineRepository.save(routine);
    }
}
