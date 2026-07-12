package com.samdasu.dodoong.domain.quest.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.quest.dto.request.DailyQuestCreateRequest;
import com.samdasu.dodoong.domain.quest.dto.response.DailyQuestCreateResponse;
import com.samdasu.dodoong.domain.quest.service.DailyQuestService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/daily-quests")
public class DailyQuestController {
    private final DailyQuestService dailyQuestService;

    @PostMapping
    public BaseResponse<DailyQuestCreateResponse> createDailyQuest(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody DailyQuestCreateRequest request
            ) {
        DailyQuestCreateResponse response = dailyQuestService.createDailyQuest(principal.memberId(), request);
        return BaseResponse.created(response);
    }
}
