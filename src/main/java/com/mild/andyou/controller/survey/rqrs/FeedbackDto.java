package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.ContentType;
import com.mild.andyou.domain.survey.FeedbackVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {

    private String text;
    private ContentType contentType;
    private String path;
    private String fileName;

    public FeedbackVo of() {
        String content = this.contentType == ContentType.YOUTUBE ? this.path : this.fileName;
        return FeedbackVo.create(text, this.contentType, content);
    }
}

