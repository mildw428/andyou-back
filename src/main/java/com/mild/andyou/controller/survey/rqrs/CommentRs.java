package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRs {
    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdAt;

    public static CommentRs convertToCommentRs(Comment comment) {
        return new CommentRs(
                comment.getId(),
                comment.getAuthor(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
} 