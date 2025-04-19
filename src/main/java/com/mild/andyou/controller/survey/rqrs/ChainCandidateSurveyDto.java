package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.Survey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChainCandidateSurveyDto {
    private Long id;
    private String title;
    private List<ChainCandidateSurveyOptionDto> options;

    public static ChainCandidateSurveyDto create(Survey survey) {
        List<ChainCandidateSurveyOptionDto> optionDtos = survey.getOptions().stream()
                .filter(o->o.getChainSurveyId() == null)
                .map(ChainCandidateSurveyOptionDto::create)
                .toList();
        return new ChainCandidateSurveyDto(
                survey.getId(),
                survey.getTitle(),
                optionDtos
        );
    }
}
