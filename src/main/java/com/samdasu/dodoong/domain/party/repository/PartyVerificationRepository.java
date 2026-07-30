package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.PartyVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PartyVerificationRepository extends JpaRepository<PartyVerification, Long> {

    boolean existsByPartyMemberIdAndVerificationDate(
            Long partyMemberId,
            LocalDate verificationDate
    );
}
