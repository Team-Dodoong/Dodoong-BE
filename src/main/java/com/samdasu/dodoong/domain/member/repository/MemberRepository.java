package com.samdasu.dodoong.domain.member.repository;

import com.samdasu.dodoong.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);

    Optional<Member> findByLoginId(String loginId);

}

