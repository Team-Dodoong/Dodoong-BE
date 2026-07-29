package com.samdasu.dodoong.domain.quest.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.quest.dto.request.DailyQuestCheckRequest;
import com.samdasu.dodoong.domain.quest.dto.request.DailyQuestCreateRequest;
import com.samdasu.dodoong.domain.quest.dto.request.DailyQuestUpdateRequest;
import com.samdasu.dodoong.domain.quest.dto.response.*;
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
        return BaseResponse.created(dailyQuestService.createDailyQuest(principal.memberId(), request));
    }

    @GetMapping("/calendar")
    public BaseResponse<DailyQuestCalendarResponse> getDailyQuestCalendar(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam("year") @Min(2000) @Max(3000) int year,
            @RequestParam("month") @Min(1) @Max(12) int month) {
        return BaseResponse.ok(dailyQuestService.getCalendar(principal.memberId(), year, month));
    }

    @GetMapping
    public BaseResponse<DailyQuestListResponse> getDailyQuestByDate(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return BaseResponse.ok(dailyQuestService.getDailyQuestByDate(principal.memberId(), date));
    }

    @GetMapping("/quadrants")
    public BaseResponse<DailyQuestQuadrantResponse> getDailyQuestQuadrant(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return BaseResponse.ok(dailyQuestService.getQuadrant(principal.memberId()));
    }

    @GetMapping("/quadrants/{questCategory}")
    public BaseResponse<Quadrant> getDailyQuestByCategory(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable QuestCategory questCategory) {
         return BaseResponse.ok(dailyQuestService.getQuadrantDetail(principal.memberId(), questCategory));
    }

    @PatchMapping("/{dailyQuestId}")
    public BaseResponse<DailyQuestSummary> updateDailyQuest(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long dailyQuestId,
            @Valid @RequestBody DailyQuestUpdateRequest request) {
        return BaseResponse.ok(dailyQuestService.updateDailyQuest(principal.memberId(), dailyQuestId, request));
    }

    @PatchMapping("/{dailyQuestId}/check")
    public BaseResponse<DailyQuestCheckResponse> checkDailyQuest(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long dailyQuestId,
            @Valid @RequestBody DailyQuestCheckRequest request) {
        return BaseResponse.ok(dailyQuestService.checkDailyQuest(principal.memberId(), dailyQuestId, request));
    }

    @PatchMapping("/{dailyQuestId}/postpone")
    public BaseResponse<DailyQuestPostponeResponse> postpone(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long dailyQuestId) {
        return BaseResponse.ok(dailyQuestService.postpone(principal.memberId(), dailyQuestId));
    }

    @DeleteMapping("/{dailyQuestId}")
    public BaseResponse<Void> deleteDailyQuest(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long dailyQuestId) {
        dailyQuestService.deleteDailyQuest(principal.memberId(), dailyQuestId);
        return BaseResponse.noContent();
    }
}
