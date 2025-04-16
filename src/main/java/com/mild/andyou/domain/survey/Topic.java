package com.mild.andyou.domain.survey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topic {
    GENERAL("일반"),
    DATING("썸·연애"),
    WORK("직장"),
    INVESTMENT("주식·투자"),
    SPORTS("스포츠"),
    PARENTING("육아"),
    SHOPPING("쇼핑"),
    TRAVEL("여행"),
    EATING("먹방"),
    MBTI("성격유형·MBTI"),
    HOBBY("취미"),
    HUMOR("유머"),
    ANIMALS("동물"),
    GAMES("게임"),
    BEAUTY("패션·뷰티"),
    MUSIC("음악"),
    CARTOONS("만화"),
    ;

    private final String description;
}
