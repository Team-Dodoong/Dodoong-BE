package com.samdasu.dodoong.domain.quest.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DayCountAccumulator {
    private long total;
    private final long checked;

    void incrementTotal() {
        total++;
    }
}