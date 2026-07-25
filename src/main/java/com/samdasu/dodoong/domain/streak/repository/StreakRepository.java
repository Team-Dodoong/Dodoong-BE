package com.samdasu.dodoong.domain.streak.repository;

import com.samdasu.dodoong.domain.streak.entity.Streak;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface StreakRepository extends JpaRepository<Streak, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Streak> findTopByMemberIdOrderByLastCheckedDateDesc(
            Long memberId
    );
}