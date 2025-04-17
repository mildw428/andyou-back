package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.ContentType;
import com.mild.andyou.domain.survey.FeedbackVo;
import com.mild.andyou.domain.survey.SurveyType;
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
    private SurveyType type;
    private String title;
    private String description;
    private ContentDto thumbnail;
    private ContentDto content;
    private List<OptionSaveRq> options;
    private FeedbackDto incorrectFeedback;

    public List<String> getFileNames() {

        List<String> fileNames = new ArrayList<>();
        if (this.getThumbnail().getContentType() == ContentType.IMAGE) {
            fileNames.add(this.getContent().getFileName());
        }

        if (this.content.getContentType() == ContentType.IMAGE && this.getContent() != null) {
            fileNames.add(this.getContent().getFileName());
        }

        this.getOptions().forEach(o -> {
            if (o.getContent().getContentType() == ContentType.IMAGE && o.getContent() != null) {
                fileNames.add(o.getContent().getFileName());
            }
        });

        return fileNames;

    }
}
