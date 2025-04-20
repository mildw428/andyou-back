package com.mild.andyou.domain.survey;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@Embeddable
@NoArgsConstructor
public class FeedbackVo {

    @Column(name = "feedback_text")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type")
    private ContentType contentType;

    @Column(name = "feedback_content")
    private String content;

    private FeedbackVo(String text, ContentType contentType, String content) {
        this.text = text;
        this.contentType = contentType;
        this.content = content;
    }

    public static FeedbackVo create(String text, ContentType contentType, String content) {
        return new FeedbackVo(text, contentType, content);
    }

    public Optional<String> getImageFileName() {
        if(this.contentType == ContentType.IMAGE) {
            return Optional.ofNullable(content);
        }
        return Optional.empty();
    }

}
