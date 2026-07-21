package com.samdasu.dodoong.domain.party.service;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.party.dto.request.PartyRequestDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyResponseDto;
import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyMember;
import com.samdasu.dodoong.domain.party.entity.PartyRole;
import com.samdasu.dodoong.domain.party.repository.PartyMemberRepository;
import com.samdasu.dodoong.domain.party.repository.PartyRepository;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyService {

    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public PartyResponseDto createParty(PartyRequestDto requestDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String encodedPassword = null;
        if (requestDto.partyPassword() != null) {
            encodedPassword = passwordEncoder.encode(requestDto.partyPassword());
        }
        Party savedParty = partyRepository.save(requestDto.toEntity(encodedPassword));

        PartyMember partyMember = PartyMember.builder()
                .role(PartyRole.LEADER)
                .member(member)
                .party(savedParty)
                .build();
        partyMemberRepository.save(partyMember);

        return PartyResponseDto.from(savedParty);
    }
}
