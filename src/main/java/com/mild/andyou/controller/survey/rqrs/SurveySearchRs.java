package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.Survey;
import com.mild.andyou.domain.survey.SurveyType;
import com.mild.andyou.domain.survey.Topic;
import com.mild.andyou.utils.s3.S3FilePath;
import com.mild.andyou.utils.s3.S3Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveySearchRs {
    private Long id;
    private SurveyType type;
    private Topic topic;
    private String title;
    private ContentDto thumbnail;
    private Long participantCount;
    private List<OptionRs> options = new ArrayList<>();

    public static SurveySearchRs convertToSurveyRs(Survey survey, Long participantCount) {
        List<OptionRs> optionRsList = survey.getOptions().stream()
                .map(option -> new OptionRs(
                        option.getId(),
                        option.getText(),
                        ContentDto.create(option.getContentVo()),
                        option.getIsCorrect(),
                        FeedbackDto.create(option.getFeedback()),
                        0,
                        0))
                .collect(Collectors.toList());

        return new SurveySearchRs(
                survey.getId(),
                survey.getType(),
                survey.getTopic(),
                survey.getTitle(),
                ContentDto.create(survey.getThumbnail()),
                participantCount,
                optionRsList
        );
    }

} 