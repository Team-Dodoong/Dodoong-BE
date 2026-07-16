package com.samdasu.dodoong.domain.character.entity;

import com.samdasu.dodoong.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member_character",
        uniqueConstraints = {@UniqueConstraint(
                        name = "uk_member_character",
                        columnNames = {"member_id", "character_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_character_id")
    private Long id;

    @Column(name = "is_equipped", nullable = false)
    private boolean equipped = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterItem characterItem;

    public MemberCharacter(Member member, CharacterItem characterItem) {
        this.member = member;
        this.characterItem = characterItem;
    }

    public void equip() {
        this.equipped = true;
    }

    public void unequip() {
        this.equipped = false;
    }
}