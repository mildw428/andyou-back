package com.mild.andyou.domain.survey;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mild.andyou.domain.survey.QSurvey.survey;
import static com.mild.andyou.domain.survey.QSurveyResponse.surveyResponse;

public class SurveyRepositoryDslImpl extends QuerydslRepositorySupport implements SurveyRepositoryDsl {

    public SurveyRepositoryDslImpl() {
        super(Survey.class);
    }

    @Override
    public Page<Survey> findBySearch(Topic topic, String keyword, Pageable pageable) {
        // 콘텐츠 쿼리
        List<Survey> content = from(survey)
                .where(
                        containKeyword(keyword),
                        eqTopic(topic)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort()))
                .fetch();

        // 카운트 쿼리
        long total = from(survey)
                .where(containKeyword(keyword))
                .fetchCount();

        // Page 객체 생성
        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public Page<Survey> findByCreatedBy(Long userId, Pageable pageable) {
        List<Survey> content = from(survey)
                .where(eqCreatedBy(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort()))
                .fetch();

        // 카운트 쿼리
        long total = from(survey)
                .where(eqCreatedBy(userId))
                .fetchCount();

        // Page 객체 생성
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Map<Long, Long> countMap(List<Survey> surveys) {
        List<Tuple> result = from(survey)
                .leftJoin(surveyResponse).on(surveyResponse.survey.eq(survey))
                .where(survey.in(surveys))
                .groupBy(survey.id)
                .select(
                        survey.id,
                        surveyResponse.countDistinct().coalesce(0L)
                )
                .fetch();

        return result.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),
                        tuple -> tuple.get(1, Long.class)
                ));
    }

    private BooleanExpression eqTopic(Topic topic) {
        if(topic == null) {
            return null;
        }
        return survey.topic.eq(topic);
    }

    private static BooleanExpression eqCreatedBy(Long userId) {
        return survey.createdBy.id.eq(userId);
    }

    private static BooleanExpression containKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return survey.title.containsIgnoreCase(keyword);
    }

    // 정렬 처리를 위한 메서드
    private OrderSpecifier<?>[] getOrderSpecifier(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();

            PathBuilder<Survey> pathBuilder = new PathBuilder<>(Survey.class, "survey");
            orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
        });

        return orders.toArray(new OrderSpecifier[0]);
    }
}
