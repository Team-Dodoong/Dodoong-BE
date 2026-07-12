package com.samdasu.dodoong.domain.quest.entity;

import com.samdasu.dodoong.domain.routine.entity.Routine;
import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import com.samdasu.dodoong.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "daily_quests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyQuest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestCategory questCategory;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(nullable = false)
    private LocalDate questDate;

    @Column(nullable = false)
    private boolean isChecked;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @Builder
    private DailyQuest(QuestCategory questCategory,
                       String content,
                       LocalDate questDate,
                       boolean isChecked,
                       Member member,
                       Routine routine) {
        this.questCategory = questCategory;
        this.content = content;
        this.questDate = questDate;
        this.isChecked = isChecked;
        this.member = member;
        this.routine = routine;
    }

    public static DailyQuest create(QuestCategory questCategory,
                                    String content,
                                    LocalDate questDate,
                                    Member member,
                                    Routine routine) {
        return DailyQuest.builder()
                .questCategory(questCategory)
                .content(content)
                .questDate(questDate)
                .member(member)
                .routine(routine)
                .build();
    }
}