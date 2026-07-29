package com.samdasu.dodoong.domain.routine.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.routine.service.RoutineService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routines")
public class RoutineController {

    private final RoutineService routineService;

    @DeleteMapping("/{routineId}")
    public BaseResponse<Void> deleteRoutine(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long routineId) {
        routineService.deleteRoutine(principal.memberId(), routineId);
        return BaseResponse.noContent();
    }
}
