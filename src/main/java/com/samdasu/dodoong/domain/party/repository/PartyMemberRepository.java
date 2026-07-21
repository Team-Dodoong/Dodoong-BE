package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.PartyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
}
