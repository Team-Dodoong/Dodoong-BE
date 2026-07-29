package com.samdasu.dodoong.domain.character.repository;

import com.samdasu.dodoong.domain.character.entity.CharacterItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterItemRepository
        extends JpaRepository<CharacterItem, Long> {
}