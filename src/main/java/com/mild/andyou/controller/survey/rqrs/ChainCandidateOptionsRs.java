package com.mild.andyou.controller.survey.rqrs;


import com.mild.andyou.domain.survey.Survey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChainCandidateOptionsRs {

    List<ChainCandidateSurveyDto> surveys;

    public static ChainCandidateOptionsRs create(List<Survey> surveys, Long chainSurveyId) {
        List<ChainCandidateSurveyDto> surveyDtos = surveys.stream()
                .map(s->ChainCandidateSurveyDto.create(s, chainSurveyId))
                .toList();

        return new ChainCandidateOptionsRs(surveyDtos);
    }
}
