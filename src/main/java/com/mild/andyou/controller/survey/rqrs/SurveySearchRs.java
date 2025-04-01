package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.Survey;
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
    private String title;
    private String thumbnailUrl;
    private List<OptionRs> options = new ArrayList<>();

    public static SurveySearchRs convertToSurveyRs(Survey survey) {
        List<OptionRs> optionRsList = survey.getOptions().stream()
                .map(option -> new OptionRs(
                        option.getId(),
                        option.getText(),
                        option.getContentVo().getContentType(),
                        S3FilePath.getSurveyContentPath(option.getContentVo().getContent()),
                        0))
                .collect(Collectors.toList());

        return new SurveySearchRs(
                survey.getId(),
                survey.getTitle(),
                S3FilePath.getSurveyContentPath(survey.getThumbnail()),
                optionRsList
        );
    }

} 