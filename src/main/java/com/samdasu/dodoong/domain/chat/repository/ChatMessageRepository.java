package com.samdasu.dodoong.domain.chat.repository;

import com.samdasu.dodoong.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
    SELECT m FROM ChatMessage m
    JOIN FETCH m.sender
    WHERE m.party.id = :partyId
        AND (:cursor IS NULL or m.id < :cursor)
    ORDER BY m.id DESC
    """)
    Slice<ChatMessage> findByPartyId(
            @Param("partyId") Long partyId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
}
