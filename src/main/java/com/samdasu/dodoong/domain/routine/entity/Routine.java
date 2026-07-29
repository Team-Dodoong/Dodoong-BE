package com.samdasu.dodoong.domain.routine.entity;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name = "routines")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Routine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuestCategory questCategory;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(nullable = false)
    private LocalDate endDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "repeat_days",
            joinColumns = @JoinColumn(name = "routine_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private Set<DayOfWeek> repeatDays = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    private Routine(QuestCategory questCategory,
                    String content,
                    Set<DayOfWeek> repeatDays,
                    LocalDate endDate,
                    Member member) {
        this.questCategory = questCategory;
        this.content = content;
        this.endDate = endDate;
        this.repeatDays = repeatDays;
        this.member = member;
    }

    public static Routine create(QuestCategory questCategory,
                                 String content,
                                 LocalDate endDate,
                                 Set<DayOfWeek> repeatDays,
                                 Member member) {
        return Routine.builder()
                .questCategory(questCategory)
                .content(content)
                .endDate(endDate)
                .repeatDays(repeatDays)
                .member(member)
                .build();
    }

    public LocalDate findNextQuestDate(LocalDate date) {
        LocalDate target = date;
        while (!target.isAfter(endDate)) {
            if (repeatDays.contains(target.getDayOfWeek())) {
                return target;
            }
            target = target.plusDays(1);
        }
        throw new CustomException(ErrorCode.ROUTINE_QUEST_DATE_NOT_FOUND);
    }

    public List<LocalDate> expandOccurrences(LocalDate from, LocalDate to) {
        LocalDate effectiveEnd = to.isBefore(endDate) ? to : endDate;

        return from.datesUntil(effectiveEnd.plusDays(1))
                .filter(date -> repeatDays.contains(date.getDayOfWeek()))
                .toList();
    }
}