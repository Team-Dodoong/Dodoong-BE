package com.samdasu.dodoong.domain.party.entity;

import com.samdasu.dodoong.global.converter.CategoryListConverter;
import com.samdasu.dodoong.global.entity.BaseTimeEntity;
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
    private int maxMembers;

    @Column(nullable = false)
    private boolean isRecruiting;

    @Column(nullable = false)
    private boolean isPublic;

    @Column
    private String partyPassword;

    @Column(nullable = false)
    private String questContent;

    @Builder
    public Party(String name, String description, List<PartyCategory> category, String imageUrl, int maxMembers, boolean isRecruiting, boolean isPublic, String partyPassword, String questContent) {
        this.name = name;
        this.description = description;
        this.category = (category != null) ? category : new ArrayList<>();
        this.imageUrl = imageUrl;
        this.maxMembers = maxMembers;
        this.isRecruiting = isRecruiting;
        this.isPublic = isPublic;
        this.partyPassword = partyPassword;
        this.questContent = questContent;
    }
}