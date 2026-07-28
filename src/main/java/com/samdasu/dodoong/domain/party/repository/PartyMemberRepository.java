package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.PartyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
    boolean existsByMemberIdAndPartyId(Long memberId, Long partyId);

    long countByPartyId(Long partyId);

    Optional<PartyMember> findByMemberIdAndPartyId(Long memberId, Long partyId);

    @Query("""
    SELECT pm.member.id
    FROM PartyMember pm
    WHERE pm.party.id = :partyId
    """)
    List<Long> findMemberIdsByPartyId(@Param("partyId") Long partyId);
}
