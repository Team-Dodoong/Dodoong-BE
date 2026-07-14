package com.samdasu.dodoong.domain.party.entity;

import lombok.Getter;

@Getter
public enum PartyCategory {
    STUDY("공부"),
    CAREER("취업"),
    DAILY("일상"),
    LANGUAGE("외국어"),
    FITNESS("운동");

    private final String description;

    PartyCategory(String description) {
        this.description = description;
    }
}