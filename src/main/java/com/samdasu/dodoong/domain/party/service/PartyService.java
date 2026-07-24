package com.samdasu.dodoong.domain.party.service;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.party.dto.request.PartyRequestDto;
import com.samdasu.dodoong.domain.party.dto.request.PartyUpdateRequestDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyListResponseDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyResponseDto;
import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyCategory;
import com.samdasu.dodoong.domain.party.entity.PartyMember;
import com.samdasu.dodoong.domain.party.entity.PartyRole;
import com.samdasu.dodoong.domain.party.repository.PartyMemberRepository;
import com.samdasu.dodoong.domain.party.repository.PartyRepository;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

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

        String encodedPassword = encodePassword(requestDto.partyPassword());
        Party savedParty = partyRepository.save(requestDto.toEntity(encodedPassword));

        PartyMember partyMember = PartyMember.builder()
                .role(PartyRole.LEADER)
                .member(member)
                .party(savedParty)
                .build();
        partyMemberRepository.save(partyMember);

        return PartyResponseDto.from(savedParty);
    }

    @Transactional
    public PartyResponseDto updateParty(Long partyId, PartyUpdateRequestDto requestDto, Long memberId) {
        Party party = findByPartyId(partyId);
        authorizePartyLeader(partyId, memberId);

        String encodedPassword = encodePassword(requestDto.partyPassword());
        party.updateParty(requestDto, encodedPassword);

        return PartyResponseDto.from(party);
    }

    @Transactional
    public void deleteParty(Long partyId, Long memberId) {
        Party party = findByPartyId(partyId);
        authorizePartyLeader(partyId, memberId);

        partyRepository.delete(party);
    }

    @Transactional(readOnly = true)
    public PartyResponseDto getPartyDetail(Long partyId) {
        Party party = findByPartyId(partyId);
        return PartyResponseDto.from(party);
    }

    @Transactional(readOnly = true)
    public Page<PartyListResponseDto> getMyParties(Long memberId, Pageable pageable) {
        Page<Party> partyPage = partyRepository.findMyParties(memberId, pageable);
        return partyPage.map(PartyListResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<PartyListResponseDto> searchParties(String keyword, List<PartyCategory> categories, Pageable pageable) {
        return partyRepository.searchParties(keyword, categories, pageable)
                .map(PartyListResponseDto::from);
    }

    public Party findByPartyId(Long partyId){
        return partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));
    }

    private void authorizePartyLeader(Long partyId, Long memberId){
        PartyMember partyMember = partyMemberRepository.findByPartyIdAndMemberId(partyId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_MEMBER_NOT_FOUND));
        if (partyMember.getRole() != PartyRole.LEADER) {
            throw new CustomException(ErrorCode.FORBIDDEN_UPDATE_PARTY);
        }
    }

    private String encodePassword(String rawPassword) {
        if (!StringUtils.hasText(rawPassword)) {
            return null;
        }
        return passwordEncoder.encode(rawPassword);
    }
}
