package com.mild.andyou.controller.comment.rqrs;

import com.mild.andyou.domain.comment.Comment;
import com.mild.andyou.domain.comment.CommentResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseRs {
    private Long id;
    private CommentResponseType responseType;
    private Long likeCount;
    private Long hateCount;

    public CommentResponseRs(Comment comment) {
        this.id = comment.getId();
        this.responseType = comment.responseType();
        this.likeCount = comment.likeCount();
        this.hateCount = comment.hateCount();
    }

}
