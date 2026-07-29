package com.samdasu.dodoong.domain.party.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.samdasu.dodoong.domain.party.entity.QParty.party;

@RequiredArgsConstructor
public class PartyRepositoryImpl implements PartyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Party> searchParties(String keyword, List<PartyCategory> categories, Pageable pageable) {
        List<Party> content = queryFactory
                .selectFrom(party)
                .where(
                        isPublicParty(),
                        keywordContains(keyword),
                        categoryIn(categories)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(party.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(party.count())
                .from(party)
                .where(
                        isPublicParty(),
                        keywordContains(keyword),
                        categoryIn(categories)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 공개된 파티만 조회
    private BooleanExpression isPublicParty() {
        return party.isPublic.isTrue();
    }

    // 제목 기반 키워드 검색
    private BooleanBuilder keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(party.name.containsIgnoreCase(keyword));
        return builder;
    }

    // 카테고리 다중 필터링
    private BooleanBuilder categoryIn(List<PartyCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        BooleanBuilder builder = new BooleanBuilder();
        for (PartyCategory category : categories) {
            // Hibernate의 타입 검증 우회
            builder.or(Expressions.stringTemplate("cast({0} as string)", party.category).contains(category.name()));
        }
        return builder;
    }
}