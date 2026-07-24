package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT p
    FROM Party p
    WHERE p.id = :partyId
    """)
    Optional<Party> findByIdForUpdate(@Param("partyId") Long partyId);
}
