package com.mild.andyou.controller.comment.rqrs;

import com.mild.andyou.domain.comment.CommentResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseRq {

    private CommentResponseType type;

}
