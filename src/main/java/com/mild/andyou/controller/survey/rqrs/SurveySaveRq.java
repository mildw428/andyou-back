package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.ContentType;
import com.mild.andyou.domain.survey.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveySaveRq {
    private Topic topic;
    private String title;
    private String description;
    private String thumbnail;
    private ContentType contentType;
    private String content;
    private List<OptionSaveRq> options;

    public List<String> getFileNames() {

        List<String> fileNames = new ArrayList<>();
        if (this.getThumbnail() != null) {
            fileNames.add(this.getThumbnail());
        }

        if (this.getContentType() == ContentType.IMAGE && this.getContent() != null) {
            fileNames.add(this.getContent());
        }

        this.getOptions().forEach(o -> {
            if (o.getContentType() == ContentType.IMAGE && o.getContent() != null) {
                fileNames.add(o.getContent());
            }
        });

        return fileNames;

    }
}
