package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long>, PartyRepositoryCustom {
    // 내가 속한 파티 목록 조회 (페이징)
    @Query("SELECT p FROM Party p WHERE p.id IN " +
            "(SELECT pm.party.id FROM PartyMember pm WHERE pm.member.id = :memberId)")
    Page<Party> findMyParties(@Param("memberId") Long memberId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")
    })
    @Query("""
    SELECT p
    FROM Party p
    WHERE p.id = :partyId
    """)
    Optional<Party> findByIdForUpdate(@Param("partyId") Long partyId);
}
