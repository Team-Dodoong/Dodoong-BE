package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.PartyVerification;
import com.samdasu.dodoong.domain.party.repository.projection.MonthlyPartyVerificationCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PartyVerificationRepository extends JpaRepository<PartyVerification, Long> {

    boolean existsByPartyMemberIdAndVerificationDate(
            Long partyMemberId,
            LocalDate verificationDate
    );

    long countByPartyIdAndVerificationDate(
            Long partyId,
            LocalDate verificationDate
    );

    @Query("""
    SELECT pv
    FROM PartyVerification pv
    JOIN FETCH pv.partyMember pm
    WHERE pm.id IN :partyMemberIds
      AND pv.verificationDate = :verificationDate
    """)
    List<PartyVerification> findByPartyMemberIdsAndVerificationDate(
            @Param("partyMemberIds") List<Long> partyMemberIds,
            @Param("verificationDate") LocalDate verificationDate
    );

    @Query("""
    SELECT pv.partyMember.member.id AS memberId,
           COUNT(pv.id) AS verificationCount
    FROM PartyVerification pv
    WHERE pv.party.id = :partyId
      AND pv.verificationDate BETWEEN :startDate AND :endDate
    GROUP BY pv.partyMember.member.id
    """)
    List<MonthlyPartyVerificationCountProjection> countMonthlyVerificationCounts(
            @Param("partyId") Long partyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM PartyVerification pv WHERE pv.party.id = :partyId")
    void deleteByPartyId(@Param("partyId") Long partyId);

    // S3 이미지 삭제를 위해 URL 가져오는 쿼리
    @Query("SELECT pv.imageUrl FROM PartyVerification pv WHERE pv.party.id = :partyId AND pv.imageUrl IS NOT NULL")
    List<String> findImageUrlsByPartyId(@Param("partyId") Long partyId);
}
