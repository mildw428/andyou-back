package com.mild.andyou.controller.survey.rqrs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentAddRq {
    private Long parentId;
    private String content;
}
