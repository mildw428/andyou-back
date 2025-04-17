package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.ContentType;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionRs {
    private Long id;
    private String text;
    private ContentDto content;
    private Boolean isCorrect;
    private FeedbackDto feedback;
    private int votes;
    private int AnyVotes;
}