package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.ContentType;
import com.mild.andyou.domain.survey.ContentVo;
import com.mild.andyou.utils.s3.S3FilePath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {

    private ContentType contentType;
    private String path;
    private String fileName;

    public static ContentDto create(String fileName) {
        return new ContentDto(ContentType.IMAGE, S3FilePath.CDN_URL_SURVEY_CONTENT_PATH, fileName);
    }

    public static ContentDto create(ContentVo contentVo) {
        String path = null;
        String fileName = null;
        switch (contentVo.getContentType()) {
            case IMAGE -> {
                path = S3FilePath.CDN_URL_SURVEY_CONTENT_PATH;
                fileName = contentVo.getContent();
            }
            case YOUTUBE -> path = contentVo.getContent();
        }
        return new ContentDto(contentVo.getContentType(), path, fileName);
    }

    public ContentVo of() {
        String content = this.contentType == ContentType.YOUTUBE ? this.path : this.fileName;
        return ContentVo.create(this.contentType, content);
    }

}
