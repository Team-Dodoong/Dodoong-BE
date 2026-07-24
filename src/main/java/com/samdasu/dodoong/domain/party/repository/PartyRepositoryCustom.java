package com.samdasu.dodoong.domain.party.repository;

import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartyRepositoryCustom {
    Page<Party> searchParties(String keyword, List<PartyCategory> categories, Pageable pageable);
}