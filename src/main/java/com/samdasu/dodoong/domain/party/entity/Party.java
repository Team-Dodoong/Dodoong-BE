package com.samdasu.dodoong.domain.party.entity;

import com.samdasu.dodoong.domain.party.dto.request.PartyUpdateRequestDto;
import com.samdasu.dodoong.global.converter.CategoryListConverter;
import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "parties")
public class Party extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = CategoryListConverter.class)
    private List<PartyCategory> category = new ArrayList<>();

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private int currentMembers;

    @Column(nullable = false)
    private int maxMembers;

    @Column(nullable = false)
    private boolean isRecruiting;

    @Column(nullable = false)
    private boolean isPublic;

    @Column
    private String partyPassword;

    @Column(nullable = false)
    private String questContent;

    @OneToMany(mappedBy = "party", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PartyMember> partyMembers = new ArrayList<>();

    @Builder
    public Party(String name, String description, List<PartyCategory> category, String imageUrl, int maxMembers, boolean isRecruiting, boolean isPublic, String partyPassword, String questContent) {
        this.name = name;
        this.description = description;
        this.category = (category != null) ? category : new ArrayList<>();
        this.imageUrl = imageUrl;
        this.currentMembers = 1;
        this.maxMembers = maxMembers;
        this.isRecruiting = isRecruiting;
        this.isPublic = isPublic;
        this.partyPassword = partyPassword;
        this.questContent = questContent;
    }

    public void updateParty(
            PartyUpdateRequestDto dto,
            String encodedPassword
    ) {
        if (dto.maxMembers() < this.currentMembers) {
            throw new CustomException(ErrorCode.INVALID_MAX_MEMBERS);
        }

        this.description = dto.description();
        this.maxMembers = dto.maxMembers();
        this.isPublic = dto.isPublic();
        this.partyPassword = dto.isPublic() ? null : encodedPassword;
        this.imageUrl = dto.imageUrl();
    }
}