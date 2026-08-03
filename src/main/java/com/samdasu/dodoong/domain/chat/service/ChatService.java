package com.samdasu.dodoong.domain.chat.service;

import com.samdasu.dodoong.domain.chat.dto.request.ChatMessageRequest;
import com.samdasu.dodoong.domain.chat.dto.response.ChatHistoryResponse;
import com.samdasu.dodoong.domain.chat.dto.response.ChatMessageHistoryResponse;
import com.samdasu.dodoong.domain.chat.dto.response.ChatMessageResponse;
import com.samdasu.dodoong.domain.chat.dto.response.ChatRoomResponse;
import com.samdasu.dodoong.domain.chat.entity.ChatMessage;
import com.samdasu.dodoong.domain.chat.event.ChatMessageCreatedEvent;
import com.samdasu.dodoong.domain.chat.repository.ChatMessageRepository;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.repository.PartyMemberRepository;
import com.samdasu.dodoong.domain.party.repository.PartyRepository;
import com.samdasu.dodoong.domain.party.repository.projection.PartyMemberCountProjection;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final MemberRepository memberRepository;
    private final PartyRepository partyRepository;
    private final ChatMessagePublisher chatMessagePublisher;
    private final ApplicationEventPublisher eventPublisher;

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

        eventPublisher.publishEvent(
                new ChatMessageCreatedEvent(partyId, ChatMessageResponse.from(saved)));
    }

    public ChatHistoryResponse getChatHistory(Long partyId, Long memberId, Long cursor, int size) {
        if (!partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)) {
            throw new CustomException(ErrorCode.PARTY_MEMBER_ONLY);
        }

        int pageSize = Math.min(Math.max(size, 1), 50);

        Slice<ChatMessage> slice = chatMessageRepository
                .findByPartyId(partyId, cursor, PageRequest.of(0, pageSize));

        List<ChatMessage> content = slice.getContent();

        Long nextCursor = (slice.hasNext() && !content.isEmpty())
                ? content.get(content.size() - 1).getId()
                : null;

        List<ChatMessageHistoryResponse> messages = content.stream()
                .sorted(Comparator.comparing(ChatMessage::getId))
                .map(message -> ChatMessageHistoryResponse.of(message, memberId))
                .toList();

        return new ChatHistoryResponse(messages, slice.hasNext(), nextCursor);
    }

    public List<ChatRoomResponse> getMyChatRooms(Long memberId) {
        List<Party> myParties = partyRepository.findAllMyParties(memberId);
        if (myParties.isEmpty()) {
            return List.of();
        }

        List<Long> partyIds = myParties.stream()
                .map(Party::getId)
                .toList();

        Map<Long, Long> memberCountMap = partyMemberRepository.countByPartyIds(partyIds).stream()
                .collect(Collectors.toMap(
                        PartyMemberCountProjection::getPartyId,
                        PartyMemberCountProjection::getMemberCount
                ));

        Map<Long, ChatMessage> lastMessageMap =
                chatMessageRepository.findLatestMessagesByPartyIds(partyIds).stream()
                        .collect(Collectors.toMap(
                                message -> message.getParty().getId(),
                                message -> message
                        ));

        return myParties.stream()
                .map(party -> ChatRoomResponse.of(
                        party,
                        memberCountMap.getOrDefault(party.getId(), 0L).intValue(),
                        lastMessageMap.get(party.getId())
                ))
                .sorted(Comparator.comparing(
                        ChatRoomResponse::lastMessageAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }
}
