package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.Survey;
import com.mild.andyou.domain.survey.SurveyOption;
import com.mild.andyou.domain.survey.SurveyType;
import com.mild.andyou.domain.survey.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyVoteRs {
    private Long participantCount;
    private List<OptionRs> options = new ArrayList<>();
    private Long chainSurveyId;

    public static SurveyVoteRs convertToSurveyRs(Survey survey, Long optionId, Long participantCount) {
        List<OptionRs> optionRsList = survey.getOptions().stream()
                .map(option -> new OptionRs(
                        option.getId(),
                        option.getText(),
                        ContentDto.create(option.getContentVo()),
                        option.getIsCorrect(),
                        FeedbackDto.create(option.getFeedback()),
                        optionId == null ? 0 : option.getResponses().size(),
                        option.getResponsesAny().size()
                ))
                .collect(Collectors.toList());

        Optional<SurveyOption> selectedOptionOpt = survey.getOptions().stream()
                .filter(o->o.getId().equals(optionId)).findAny();

        return new SurveyVoteRs(
                participantCount,
                optionRsList,
                selectedOptionOpt.map(SurveyOption::getChainSurveyId).orElse(null)
        );
    }

} 