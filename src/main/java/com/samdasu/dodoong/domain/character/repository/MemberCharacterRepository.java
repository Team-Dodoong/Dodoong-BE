package com.samdasu.dodoong.domain.character.repository;

import com.samdasu.dodoong.domain.character.entity.MemberCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberCharacterRepository
        extends JpaRepository<MemberCharacter, Long> {

    @Query("""
            select mc
            from MemberCharacter mc
            join fetch mc.characterItem
            where mc.member.id = :memberId
            """)
    List<MemberCharacter> findAllByMemberId(@Param("memberId") Long memberId);
}