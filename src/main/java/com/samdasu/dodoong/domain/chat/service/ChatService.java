package com.samdasu.dodoong.domain.chat.service;

import com.samdasu.dodoong.domain.chat.dto.request.ChatMessageRequest;
import com.samdasu.dodoong.domain.chat.dto.response.ChatMessageResponse;
import com.samdasu.dodoong.domain.chat.entity.ChatMessage;
import com.samdasu.dodoong.domain.chat.repository.ChatMessageRepository;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.repository.PartyMemberRepository;
import com.samdasu.dodoong.domain.party.repository.PartyRepository;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final MemberRepository memberRepository;
    private final PartyRepository partyRepository;
    private final ChatMessagePublisher chatMessagePublisher;

    @Transactional
    public void sendMessage(Long partyId, Long senderId, ChatMessageRequest request) {

        if (!partyMemberRepository.existsByMemberIdAndPartyId(senderId, partyId)) {
            throw new CustomException(ErrorCode.PARTY_MEMBER_ONLY);
        }

        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Party partyRef = partyRepository.getReferenceById(partyId);

        ChatMessage saved = chatMessageRepository.save(
                ChatMessage.builder()
                        .party(partyRef)
                        .sender(sender)
                        .content(request.content())
                        .build()
        );

        chatMessagePublisher.broadcastToParty(partyId, ChatMessageResponse.from(saved));
    }
}
