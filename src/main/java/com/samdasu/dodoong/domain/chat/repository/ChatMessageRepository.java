package com.samdasu.dodoong.domain.chat.repository;

import com.samdasu.dodoong.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    @Query("""
    SELECT m FROM ChatMessage m
    WHERE m.id IN (
        SELECT MAX(m2.id)
        FROM ChatMessage m2
        WHERE m2.party.id IN :partyIds
        GROUP BY m2.party.id)
    """)
    List<ChatMessage> findLatestMessagesByPartyIds(
            @Param("partyIds") List<Long> partyIds
    );

    @Modifying
    @Query("DELETE FROM ChatMessage c WHERE c.party.id = :partyId")
    void deleteByPartyId(@Param("partyId") Long partyId);
}
