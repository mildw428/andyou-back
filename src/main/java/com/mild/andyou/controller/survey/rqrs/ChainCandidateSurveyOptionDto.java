package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.SurveyOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChainCandidateSurveyOptionDto {
    private Long id;
    private String text;

    public static ChainCandidateSurveyOptionDto create(SurveyOption surveyOption) {
        return new ChainCandidateSurveyOptionDto(surveyOption.getId(), surveyOption.getText());
    }
}
