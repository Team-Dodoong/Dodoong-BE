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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyQuestService {

    private final MemberRepository memberRepository;
    private final DailyQuestRepository dailyQuestRepository;
    private final RoutineService routineService;

    @Transactional
    public DailyQuestCreateResponse createDailyQuest(Long memberId,
                                                     DailyQuestCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!request.isRoutine()) {
            return createSingleQuest(member, request);
        }
        return createRoutineQuests(member, request);
    }

    // 퀘스트 단일 생성
    private DailyQuestCreateResponse createSingleQuest(
            Member member,
            DailyQuestCreateRequest request
    ) {
        DailyQuest dailyQuest = DailyQuest.create(
                request.questCategory(),
                request.content(),
                LocalDate.now(),
                member,
                null
        );
        DailyQuest saved = dailyQuestRepository.save(dailyQuest);
        return DailyQuestCreateResponse.ofSingle(saved);
    }

    // 퀘스트 루틴 생성
    private DailyQuestCreateResponse createRoutineQuests(Member member,
                                                         DailyQuestCreateRequest request) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        Routine routine = routineService.createRoutine(
                request.questCategory(),
                request.content(),
                request.endDate(),
                request.repeatDays(),
                member
        );

        LocalDate firstQuestDate = routine.findNextQuestDate(today);

        if (firstQuestDate.equals(today)) {
            dailyQuestRepository.save(DailyQuest.create(
                    routine.getQuestCategory(),
                    routine.getContent(),
                    today,
                    member,
                    routine
            ));
        }

        return DailyQuestCreateResponse.ofRoutine(routine, firstQuestDate);
    }
}
