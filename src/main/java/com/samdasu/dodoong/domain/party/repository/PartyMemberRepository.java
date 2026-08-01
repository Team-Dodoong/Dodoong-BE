package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.PartyMember;
import com.samdasu.dodoong.domain.party.entity.PartyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
}
