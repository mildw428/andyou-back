package com.mild.andyou.application.comment;

import com.mild.andyou.config.filter.UserContextHolder;
import com.mild.andyou.controller.comment.rqrs.CommentResponseRq;
import com.mild.andyou.controller.comment.rqrs.CommentResponseRs;
import com.mild.andyou.controller.survey.rqrs.CommentAddRq;
import com.mild.andyou.controller.survey.rqrs.CommentRs;
import com.mild.andyou.domain.comment.Comment;
import com.mild.andyou.domain.comment.CommentRepository;
import com.mild.andyou.domain.comment.CommentResponse;
import com.mild.andyou.domain.comment.CommentResponseRepository;
import com.mild.andyou.domain.survey.Survey;
import com.mild.andyou.domain.survey.SurveyRepository;
import com.mild.andyou.domain.user.User;
import com.mild.andyou.utils.PageRq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final SurveyRepository surveyRepository;
    private final CommentRepository commentRepository;
    private final CommentResponseRepository commentResponseRepository;

    public Page<CommentRs> getComments(Long surveyId, PageRq pageRq) {
        Page<Comment> comments = commentRepository.findBySurveyIdAndParentIsNull(surveyId, pageRq.toPageable());

        return comments.map(CommentRs::convertToCommentRs);
    }

    // 댓글 작성
    @Transactional
    public CommentRs addComment(Long surveyId, CommentAddRq rq) {
        Optional<Survey> surveyOpt = surveyRepository.findById(surveyId);
        Survey survey = surveyOpt.get();
        Comment parent = null;
        if(rq.getParentId()!=null) {
            parent = commentRepository.findById(rq.getParentId()).orElseThrow();
        }
        User mention = null;
        if(rq.getMentionUserId() != null) {
            mention = new User(rq.getMentionUserId());
        }

        Comment comment = Comment.create(
                survey,
                rq.getContent(),
                parent,
                mention
        );
        commentRepository.save(comment);
        return CommentRs.convertToCommentRs(comment);
    }

    public CommentResponseRs commentResponse(Long id, CommentResponseRq rq) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        CommentResponse response = commentResponseRepository.findByComment_IdAndUser_Id(id, UserContextHolder.userId())
                .orElseGet(()->CommentResponse.create(comment));
        switch (rq.getType()) {
            case LIKE -> response.like();
            case HATE -> response.hate();
            case NONE -> response.delete();
        }
        commentResponseRepository.save(response);
        return new CommentResponseRs(comment);
    }

}
