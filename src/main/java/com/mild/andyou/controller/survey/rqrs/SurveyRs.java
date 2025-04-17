package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.ContentType;
import com.mild.andyou.domain.survey.Survey;
import com.mild.andyou.domain.survey.SurveyType;
import com.mild.andyou.domain.survey.Topic;
import com.mild.andyou.utils.s3.S3FilePath;
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
public class SurveyRs {
    private Long id;
    private SurveyType type;
    private Topic topic;
    private ContentDto thumbnail;
    private String title;
    private String description;
    private ContentDto content;
    private Long optionId;
    private Long participantCount;
    private List<OptionRs> options = new ArrayList<>();

    public static SurveyRs convertToSurveyRs(Survey survey, Long optionId, Long participantCount) {
        List<OptionRs> optionRsList = survey.getOptions().stream()
                .map(option -> new OptionRs(
                        option.getId(),
                        option.getText(),
                        ContentDto.create(option.getContentVo()),
                        option.getIsCorrect(),
                        FeedbackDto.create(option.getFeedback()),
                        optionId == null ? 0 : option.getResponses().size() + option.getResponsesAny().size()
                ))
                .collect(Collectors.toList());

        return new SurveyRs(
                survey.getId(),
                survey.getType(),
                survey.getTopic(),
                ContentDto.create(survey.getThumbnail()),
                survey.getTitle(),
                survey.getDescription(),
                ContentDto.create(survey.getContentVo()),
                optionId,
                participantCount,
                optionRsList
        );
    }

} 