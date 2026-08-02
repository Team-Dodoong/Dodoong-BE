package com.samdasu.dodoong.domain.streak.repository;

import com.samdasu.dodoong.domain.streak.entity.Streak;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface StreakRepository extends JpaRepository<Streak, Long> {

    // 회원의 가장 최근 스트릭 조회
    Optional<Streak> findTopByMemberIdOrderByLastCheckedDateDesc(Long memberId);

    // 회원 탈퇴 시 회원의 스트릭 전체 삭제
    void deleteAllByMemberId(Long memberId);
}