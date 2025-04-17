package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.ContentType;
import com.mild.andyou.domain.survey.FeedbackVo;
import com.mild.andyou.utils.s3.S3FilePath;
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

    public static FeedbackDto create(FeedbackVo feedbackVo) {
        String path = null;
        String fileName = null;
        if(feedbackVo == null) {
            return new FeedbackDto(null, ContentType.NONE, null, null);
        }

        switch (feedbackVo.getContentType()) {
            case IMAGE -> {
                path = S3FilePath.CDN_URL_SURVEY_CONTENT_PATH;
                fileName = feedbackVo.getContent();
            }
            case YOUTUBE -> path = feedbackVo.getContent();
        }
        return new FeedbackDto(feedbackVo.getText(), feedbackVo.getContentType(), path, fileName);
    }

    public FeedbackVo of() {
        String content = this.contentType == ContentType.YOUTUBE ? this.path : this.fileName;
        return FeedbackVo.create(text, this.contentType, content);
    }
}

