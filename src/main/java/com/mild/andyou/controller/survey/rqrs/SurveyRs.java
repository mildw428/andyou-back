package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.ContentType;
import com.mild.andyou.domain.survey.Survey;
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
    private String title;
    private String description;
    private ContentType contentType;
    private String contentUrl;
    private Long optionId;
    private List<OptionRs> options = new ArrayList<>();

    public static SurveyRs convertToSurveyRs(Survey survey, Long optionId) {
        List<OptionRs> optionRsList = survey.getOptions().stream()
                .map(option -> new OptionRs(
                        option.getId(),
                        option.getText(),
                        option.getContentVo().getContentType(),
                        option.getContentVo().getContentType() == ContentType.IMAGE ?
                                S3FilePath.getSurveyContentPath(option.getContentVo().getContent()) :
                                option.getContentVo().getContent(),
                        optionId == null ? 0 : option.getResponses().size()))
                .collect(Collectors.toList());

        return new SurveyRs(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getContentVo().getContentType(),
                survey.getContentVo().getContentType() == ContentType.IMAGE ?
                        S3FilePath.getSurveyContentPath(survey.getContentVo().getContent()) :
                        survey.getContentVo().getContent(),
                optionId,
                optionRsList
        );
    }

} 