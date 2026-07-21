package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {
}
