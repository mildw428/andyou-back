package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.survey.ContentType;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionRs {
    private Long id;
    private String text;
    private ContentType contentType;
    private String contentUrl;
    private int votes;
}