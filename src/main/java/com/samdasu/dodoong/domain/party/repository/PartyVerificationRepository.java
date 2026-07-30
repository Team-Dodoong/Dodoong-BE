package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.PartyVerification;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
