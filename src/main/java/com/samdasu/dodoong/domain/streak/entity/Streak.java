package com.samdasu.dodoong.domain.streak.entity;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name ="streaks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Streak extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate lastCheckedDate;

    @Column(nullable = false)
    private int consecutiveDays;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private Streak(Member member,
                   LocalDate startDate,
                   LocalDate lastCheckedDate,
                   int consecutiveDays
    ) {
        this.member = member;
        this.startDate = startDate;
        this.lastCheckedDate = lastCheckedDate;
        this.consecutiveDays = consecutiveDays;
    }

    //스트릭 생성
    public static Streak start(Member member, LocalDate achievementDate) {
        return new Streak(member, achievementDate, achievementDate, 1);
    }

    //스트릭 반영 여부 확인
    public boolean isAlreadyReflected(LocalDate achievementDate) {
        return lastCheckedDate.isEqual(achievementDate);
    }

    // 기존 스트릭과 연속된 날짜인지 확인
    public boolean canContinue(LocalDate achievementDate) {
        return lastCheckedDate
                .plusDays(1)
                .isEqual(achievementDate);
    }

    //스트릭 증가
    public void continueStreak(LocalDate achievementDate) {
        this.lastCheckedDate = achievementDate;
        this.consecutiveDays++;
    }
}
