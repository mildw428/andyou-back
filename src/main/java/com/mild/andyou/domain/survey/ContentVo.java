package com.mild.andyou.domain.survey;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class ContentVo {

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private String content;

    private ContentVo(ContentType contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    public static ContentVo create(ContentType contentType, String content) {
        return new ContentVo(contentType, content);
    }
}
