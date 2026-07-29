package com.samdasu.dodoong.domain.quest.entity;

import com.samdasu.dodoong.domain.routine.entity.Routine;
import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "daily_quests",
uniqueConstraints = @UniqueConstraint(
        name = "uk_daily_quests_routine_quest_date",
        columnNames = {"routine_id", "quest_date"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyQuest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
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

    public void updateQuest(QuestCategory questCategory,
                                    String content) {
        if (questCategory != null) {
            this.questCategory = questCategory;
        }
        if (content != null) {
            this.content = content;
        }
    }

    public boolean changeChecked(boolean isChecked) {
        if (this.isChecked == isChecked) {
            return false;
        }
        this.isChecked = isChecked;
        return true;
    }

    public boolean isFromRoutine() {
        return this.routine != null;
    }

    public void postponeToNextDay() {
        if (isFromRoutine()) {
            throw new CustomException(ErrorCode.ROUTINE_QUEST_CANNOT_BE_POSTPONED);
        }
        if (this.isChecked) {
            throw new CustomException(ErrorCode.CHECKED_QUEST_CANNOT_BE_POSTPONED);
        }
        this.questDate = this.questDate.plusDays(1);
    }

    public void checkDeletable() {
        if (isChecked) {
            throw new CustomException(ErrorCode.DAILY_QUEST_CANNOT_BE_DELETED);
        }
    }
}