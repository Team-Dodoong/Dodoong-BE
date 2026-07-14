package com.samdasu.dodoong.domain.party.entity;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "party_members")
public class PartyMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @Builder
    public PartyMember(PartyRole role, Member member, Party party) {
        this.role = role;
        this.member = member;
        this.party = party;
    }
}
