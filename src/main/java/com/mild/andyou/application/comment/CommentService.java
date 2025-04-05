package com.mild.andyou.application.comment;

import com.mild.andyou.controller.survey.rqrs.CommentRs;
import com.mild.andyou.domain.comment.Comment;
import com.mild.andyou.domain.comment.CommentRepository;
import com.mild.andyou.domain.survey.Survey;
import com.mild.andyou.domain.survey.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final SurveyRepository surveyRepository;
    private final CommentRepository commentRepository;

    public List<CommentRs> getComments(Long surveyId) {
        List<Comment> comments = commentRepository.findBySurveyId(surveyId);

        return comments.stream().map(CommentRs::convertToCommentRs).collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public CommentRs addComment(Long surveyId, String content) {
        Optional<Survey> surveyOpt = surveyRepository.findById(surveyId);
        Survey survey = surveyOpt.get();

        Comment comment = new Comment(
                survey,
                content,
                null
        );
        commentRepository.save(comment);
        return CommentRs.convertToCommentRs(comment);
    }

}
