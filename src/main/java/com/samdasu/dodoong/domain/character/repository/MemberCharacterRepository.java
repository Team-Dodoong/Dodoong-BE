package com.samdasu.dodoong.domain.character.repository;

import com.samdasu.dodoong.domain.character.entity.MemberCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberCharacterRepository
        extends JpaRepository<MemberCharacter, Long> {

    @Query("""
            select mc
            from MemberCharacter mc
            join fetch mc.characterItem
            where mc.member.id = :memberId
            """)
    List<MemberCharacter> findAllByMemberId(@Param("memberId") Long memberId);

    //캐릭터 상세 조회
    Optional<MemberCharacter> findByMemberIdAndCharacterItemId(
            Long memberId,
            Long characterId
    );

    @Query("""
            select mc
            from MemberCharacter mc
            join fetch mc.characterItem
            where mc.member.id = :memberId
                and mc.isEquipped = true
            """)
    Optional<MemberCharacter> findEquippedByMemberId(
            @Param("memberId") Long memberId
    );

    void deleteAllByMemberId(Long memberId);
}