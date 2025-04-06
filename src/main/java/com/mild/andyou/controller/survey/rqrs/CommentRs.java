package com.mild.andyou.controller.survey.rqrs;

import com.mild.andyou.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRs {
    private Long id;
    private String author;
    private String content;
    private Long parentId;
    private List<CommentRs> children;
    private LocalDateTime createdAt;

    public static CommentRs convertToCommentRs(Comment comment) {
        List<CommentRs> childrens = comment.getChildren().stream()
                .map(CommentRs::convertToCommentRs)
                .toList();

        return new CommentRs(
                comment.getId(),
                comment.getAuthor(),
                comment.getContent(),
                comment.getParent() ==null ? null : comment.getParent().getId(),
                childrens,
                comment.getCreatedAt()
        );
    }
} 