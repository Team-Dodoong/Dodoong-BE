package com.samdasu.dodoong.domain.chat.entity;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_messages")
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member sender;

    @Builder
    public ChatMessage(String content, Party party, Member sender) {
        this.content = content;
        this.party = party;
        this.sender = sender;
    }
}
