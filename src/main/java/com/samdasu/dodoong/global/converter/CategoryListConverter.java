package com.samdasu.dodoong.global.converter;

import com.samdasu.dodoong.domain.party.entity.PartyCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class CategoryListConverter implements AttributeConverter<List<PartyCategory>, String> {

    private static final String SPLIT_CHAR = ",";

    // [PartyCategory.LANGUAGE, PartyCategory.STUDY] -> "LANGUAGE,STUDY"
    @Override
    public String convertToDatabaseColumn(List<PartyCategory> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream()
                .map(PartyCategory::name)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    // "LANGUAGE,STUDY" -> [PartyCategory.LANGUAGE, PartyCategory.STUDY]
    @Override
    public List<PartyCategory> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .map(String::trim)
                .map(PartyCategory::valueOf)
                .collect(Collectors.toList());
    }
}