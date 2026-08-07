package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.PartyMember;
import com.samdasu.dodoong.domain.party.repository.projection.PartyMemberCountProjection;
import com.samdasu.dodoong.domain.party.entity.PartyRole;
import org.springframework.data.jpa.repository.Modifying;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
    boolean existsByMemberIdAndPartyId(Long memberId, Long partyId);

    long countByPartyId(Long partyId);

    Optional<PartyMember> findByMemberIdAndPartyId(Long memberId, Long partyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")
    })
    @Query("""
    SELECT pm
    FROM PartyMember pm
    JOIN FETCH pm.member
    JOIN FETCH pm.party
    WHERE pm.member.id = :memberId
      AND pm.party.id = :partyId
    """)
    Optional<PartyMember> findByMemberIdAndPartyIdForUpdate(
            @Param("memberId") Long memberId,
            @Param("partyId") Long partyId
    );

    @Query("""
    SELECT pm
    FROM PartyMember pm
    JOIN FETCH pm.member
    WHERE pm.party.id = :partyId
      AND (:cursor IS NULL OR pm.id > :cursor)
    ORDER BY pm.id ASC
    """)
    List<PartyMember> findVerificationPageByPartyId(
            @Param("partyId") Long partyId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    @Query("""
    SELECT pm
    FROM PartyMember pm
    JOIN FETCH pm.member
    WHERE pm.party.id = :partyId
    ORDER BY pm.id ASC
    """)
    List<PartyMember> findAllByPartyIdWithMember(@Param("partyId") Long partyId);

    @Query("""
    SELECT pm.member.id
    FROM PartyMember pm
    WHERE pm.party.id = :partyId
    """)
    List<Long> findMemberIdsByPartyId(@Param("partyId") Long partyId);

    @Query("""
    SELECT pm.party.id AS partyId, COUNT(pm) AS memberCount
    FROM PartyMember pm 
    WHERE pm.party.id IN :partyIds
    GROUP BY pm.party.id
    """)
    List<PartyMemberCountProjection> countByPartyIds(
            @Param("partyIds") List<Long> partyIds
    );
    //회원 탈퇴 데이터 삭제 관련 메서드
    // 해당 회원이 파티장인 파티가 있는지 확인
    boolean existsByMemberIdAndRole(Long memberId, PartyRole role);

    // 회원이 일반 멤버로 참여 중인 파티 목록 조회
    @Query("""
            SELECT pm
            FROM PartyMember pm
            JOIN FETCH pm.party
            WHERE pm.member.id = :memberId
                AND pm.role = :role
           """)
    List<PartyMember> findAllByMemberIdAndRole(@Param("memberId") Long memberId, @Param("role") PartyRole role);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM PartyMember pm WHERE pm.party.id = :partyId")
    void deleteByPartyId(@Param("partyId") Long partyId);
}
