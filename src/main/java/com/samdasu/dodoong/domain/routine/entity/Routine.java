package com.samdasu.dodoong.domain.routine.entity;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.quest.entity.QuestCategory;
import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
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
    @Column(nullable = false, length = 20)
    private QuestCategory questCategory;

    @Column(nullable = false, length = 50)
    private String content;

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
                    Member member) {
        this.questCategory = questCategory;
        this.content = content;
        this.repeatDays = repeatDays;
        this.member = member;
    }

    public static Routine create(QuestCategory questCategory,
                                 String content,
                                 Set<DayOfWeek> repeatDays,
                                 Member member) {
        return Routine.builder()
                .questCategory(questCategory)
                .content(content)
                .repeatDays(repeatDays)
                .member(member)
                .build();
    }

    public LocalDate findNextQuestDate(LocalDate date) {
        LocalDate target = date;
        for (int i = 0; i < 7; i++) {
            if (repeatDays.contains(target.getDayOfWeek())) {
                return target;
            }
            target = target.plusDays(1);
        }
        throw new IllegalStateException("반복 요일이 비어있습니다.");
    }
}