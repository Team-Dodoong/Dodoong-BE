package com.samdasu.dodoong.domain.routine.service;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.quest.repository.DailyQuestRepository;
import com.samdasu.dodoong.domain.routine.entity.Routine;
import com.samdasu.dodoong.domain.routine.repository.RoutineRepository;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
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
    private final DailyQuestRepository dailyQuestRepository;

    @Transactional
    public Routine createRoutine(QuestCategory questCategory,
                                 String content,
                                 LocalDate endDate,
                                 Set<DayOfWeek> repeatDays,
                                 Member member) {
        Routine routine = Routine.create(questCategory, content, endDate, repeatDays, member);
        return routineRepository.save(routine);
    }

    @Transactional
    public void deleteRoutine(Long memberId, Long routineId) {
        Routine routine = routineRepository.findByIdAndMemberId(routineId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROUTINE_NOT_FOUND));
        // 미완료 퀘스트 삭제
        dailyQuestRepository.deleteUncheckedByRoutineId(routineId);
        // 완료 퀘스트는 링크만 끊음
        dailyQuestRepository.detachCheckedByRoutineId(routineId);
        // 루틴 본체 삭제
        routineRepository.delete(routine);
    }
}
