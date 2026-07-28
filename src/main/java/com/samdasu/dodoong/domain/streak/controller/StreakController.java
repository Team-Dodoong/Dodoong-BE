package com.samdasu.dodoong.domain.streak.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.streak.dto.StreakResponse;
import com.samdasu.dodoong.domain.streak.service.StreakService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/streaks")
public class StreakController {

    private final StreakService streakService;

    @GetMapping
    public BaseResponse<StreakResponse> getStreak(@AuthenticationPrincipal CustomUserPrincipal principal){
        StreakResponse response = streakService.getStreak(principal.memberId());

        return BaseResponse.ok(response);
    }
}
