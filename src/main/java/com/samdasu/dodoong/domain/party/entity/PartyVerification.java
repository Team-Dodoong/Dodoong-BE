package com.samdasu.dodoong.domain.party.entity;

import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "party_verifications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"party_member_id", "verification_date"})
)
public class PartyVerification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_verification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_member_id", nullable = false)
    private PartyMember partyMember;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private LocalDate verificationDate;

    @Builder
    public PartyVerification(
            Party party,
            PartyMember partyMember,
            String imageUrl,
            boolean verified,
            LocalDate verificationDate
    ) {
        this.party = party;
        this.partyMember = partyMember;
        this.imageUrl = imageUrl;
        this.verified = verified;
        this.verificationDate = verificationDate;
    }
}
