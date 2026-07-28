package com.samdasu.dodoong.domain.streak.dto;

import com.samdasu.dodoong.domain.streak.entity.Streak;

import java.time.LocalDate;

public record StreakResponse(LocalDate startDate, LocalDate lastCheckedDate, int consecutiveDays) {
    public static StreakResponse from(Streak streak){
        return new StreakResponse(streak.getStartDate(), streak.getLastCheckedDate(), streak.getConsecutiveDays());
    }

    //스트릭 없을 경우
    public static StreakResponse empty(){
        return new StreakResponse(null, null, 0);
    }
}
