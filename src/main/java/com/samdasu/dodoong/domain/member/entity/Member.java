package com.samdasu.dodoong.domain.member.entity;

import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name= "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String loginId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, length = 50)
    private String nickname;

    @Column
    private String profileImageKey;

    @Column(length = 255)
    private String introduction;

    @Column(nullable = false)
    private int level = 1;

    @Column(nullable = false)
    private int experience = 0;

    @Column(nullable = false)
    private int coin = 0;


    @Builder
    public Member(String loginId, String encodedPassword) {
        this.loginId = loginId;
        this.password = encodedPassword;
    }

    public void updateProfile(String nickname, String profileImageKey, String introduction) {
        if (nickname != null) {
            this.nickname = nickname;
        }

        if (profileImageKey != null && !profileImageKey.isBlank()) {
            this.profileImageKey = profileImageKey;
        }

        if (introduction != null) {
            this.introduction = introduction;
        }
    }

    public void addExperience(int amount) {
        if (amount <= 0) {
            return;
        }

        this.experience += amount;

        while (canLevelUp()) {
            int requiredExperience = calculateRequiredExperience();
            this.experience -= requiredExperience;
            this.level += 1;
            earnCoin(levelUpReward());
        }
    }

    public void subtractExperience(int amount) {
        if (amount <= 0) {
            return;
        }

        int remaining = amount;

        while (remaining > this.experience && this.level > 1) {
            remaining -= this.experience;
            spendCoin(levelUpReward());
            this.level -= 1;
            this.experience = calculateRequiredExperience();
        }

        this.experience = Math.max(0, this.experience - remaining);
    }

    //코인 보유량 검사
    public boolean hasEnoughCoin(int amount){
        return this.coin >= amount;
    }
    //코인 감소
    public void spendCoin(int amount){
        this.coin -= amount;
    }
    //코인 증가
    public void earnCoin(int amount) {
        this.coin += amount;
    }

    // 레벨업
    public int calculateRequiredExperience() {
        return (this.level + 1) * 100;
    }

    public boolean canLevelUp() {
        return this.experience >= calculateRequiredExperience();
    }

    private int levelUpReward() {
        return this.level * 10;
    }
}
