package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.ContentType;
import com.mild.andyou.domain.survey.FeedbackVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionSaveRq {
    private String text;
    private ContentDto content;
    private Boolean isCorrect;
    private FeedbackDto feedback;
}