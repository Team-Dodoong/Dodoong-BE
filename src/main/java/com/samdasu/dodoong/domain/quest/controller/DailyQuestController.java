package com.samdasu.dodoong.domain.quest.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.quest.dto.request.DailyQuestCreateRequest;
import com.samdasu.dodoong.domain.quest.dto.response.*;
import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.domain.quest.service.DailyQuestService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Validated
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

    @GetMapping("/calendar")
    public BaseResponse<DailyQuestCalendarResponse> getDailyQuestCalendar(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam("year") @Min(2000) @Max(3000) int year,
            @RequestParam("month") @Min(1) @Max(12) int month) {
        DailyQuestCalendarResponse response = dailyQuestService.getCalendar(principal.memberId(), year, month);
        return BaseResponse.ok(response);
    }

    @GetMapping
    public BaseResponse<DailyQuestListResponse> getDailyQuestByDate(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyQuestListResponse response = dailyQuestService.getDailyQuestByDate(principal.memberId(), date);
        return BaseResponse.ok(response);
    }

    @GetMapping("/quadrants")
    public BaseResponse<DailyQuestQuadrantResponse> getDailyQuestQuadrant(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        DailyQuestQuadrantResponse response = dailyQuestService.getQuadrant(principal.memberId());
        return BaseResponse.ok(response);
    }

    @GetMapping("/quadrants/{questCategory}")
    public BaseResponse<Quadrant> getDailyQuestByCategory(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable QuestCategory questCategory) {
         return BaseResponse.ok(dailyQuestService.getQuadrantDetail(principal.memberId(), questCategory));
    }
}
