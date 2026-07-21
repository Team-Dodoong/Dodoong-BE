package com.samdasu.dodoong.domain.character.service;

import com.samdasu.dodoong.domain.character.dto.response.*;
import com.samdasu.dodoong.domain.character.entity.CharacterItem;
import com.samdasu.dodoong.domain.character.entity.MemberCharacter;
import com.samdasu.dodoong.domain.character.repository.CharacterItemRepository;
import com.samdasu.dodoong.domain.character.repository.MemberCharacterRepository;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterService {

    private final CharacterItemRepository characterItemRepository;
    private final MemberCharacterRepository memberCharacterRepository;

    //전체 캐릭터 목록 조회
    public CharacterListResponse getCharacters(Long memberId) {
        List<CharacterItem> characterItems = characterItemRepository.findAll();

        List<MemberCharacter> memberCharacters = memberCharacterRepository.findAllByMemberId(memberId);

        Map<Long, MemberCharacter> ownedCharacterMap =
                memberCharacters.stream().collect(Collectors.toMap(
                        memberCharacter -> memberCharacter.getCharacterItem().getId(), Function.identity()
                ));

        List<CharacterListItemResponse> characters =
                characterItems.stream()
                        .map(characterItem -> {
                            MemberCharacter memberCharacter = ownedCharacterMap.get(characterItem.getId());

                            boolean owned = memberCharacter != null;
                            boolean isEquipped = owned && memberCharacter.isEquipped();

                            return CharacterListItemResponse.of(
                                    characterItem,
                                    owned,
                                    isEquipped
                            );
                        })
                        .toList();

        return CharacterListResponse.from(characters);
    }

    //캐릭터 상세 조회
    public CharacterDetailResponse getCharacter(Long memberId, Long characterId) {
        CharacterItem characterItem = characterItemRepository.findById(characterId).
                orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));

        MemberCharacter memberCharacter = memberCharacterRepository.findByMemberIdAndCharacterItemId(memberId, characterId)
                .orElse(null);

        boolean owned = memberCharacter != null;

        boolean isEquipped = owned && memberCharacter.isEquipped();

        return CharacterDetailResponse.of(
                characterItem,
                owned,
                isEquipped
        );
    }

    //장착 중인 캐릭터 조회
    public EquippedCharacterResponse getEquippedCharacter(Long memberId){
        MemberCharacter memberCharacter = memberCharacterRepository.findEquippedByMemberId(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.EQUIPPED_CHARACTER_NOT_FOUND));

        return EquippedCharacterResponse.from(memberCharacter.getCharacterItem());
    }

    //보유 캐릭터 조회
    public CharacterListResponse getOwnedCharacters(Long memberId){
        List<OwnedCharacterListItemResponse> characters = memberCharacterRepository.findAllByMemberId(memberId)
                .stream()
                .map(OwnedCharacterListItemResponse::from)
                .toList();

        return new CharacterListResponse(characters);
    }
}