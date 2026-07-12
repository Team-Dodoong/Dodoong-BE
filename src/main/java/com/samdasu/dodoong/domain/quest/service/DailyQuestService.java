package com.samdasu.dodoong.domain.quest.service;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.quest.dto.request.DailyQuestCreateRequest;
import com.samdasu.dodoong.domain.quest.dto.response.DailyQuestCreateResponse;
import com.samdasu.dodoong.domain.quest.repository.DailyQuestRepository;
import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.routine.entity.Routine;
import com.samdasu.dodoong.domain.routine.service.RoutineService;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyQuestService {

    private final MemberRepository memberRepository;
    private final DailyQuestRepository dailyQuestRepository;
    private final RoutineService routineService;

    @Transactional
    public DailyQuestCreateResponse createDailyQuest(Long memberId, DailyQuestCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Routine routine = null;
        LocalDate targetDate = request.questDate();

        if (request.isRoutine()) {
            routine = routineService.createRoutine(
                    request.questCategory(),
                    request.content(),
                    request.repeatDays(),
                    member
            );
            targetDate = routine.findNextQuestDate(request.questDate());
        }

        DailyQuest dailyQuest = DailyQuest.create(
                request.questCategory(),
                request.content(),
                targetDate,
                member,
                routine
        );
        DailyQuest saved = dailyQuestRepository.save(dailyQuest);
        return DailyQuestCreateResponse.of(saved, routine);
    }
}
