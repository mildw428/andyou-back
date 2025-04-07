package com.mild.andyou.domain.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentResponseType {
    LIKE,
    HATE,
    NONE
}
