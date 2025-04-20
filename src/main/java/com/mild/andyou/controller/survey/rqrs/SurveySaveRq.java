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
    private Long chainOptionId;
    private Topic topic;
    private SurveyType type;
    private String title;
    private String description;
    private List<OptionSaveRq> options;
    private FeedbackDto incorrectFeedback;

}
