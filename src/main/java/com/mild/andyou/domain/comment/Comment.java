package com.mild.andyou.domain.comment;

import com.mild.andyou.domain.survey.Survey;
import com.mild.andyou.domain.user.User;
import com.mild.andyou.config.filter.UserContextHolder;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mention_user_id")
    private User mention;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Comment> children = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY)
    private List<CommentResponse> responses = new ArrayList<>();

    private Boolean isDeleted;

    private Comment(Survey survey, User user, String content, Comment parent, User mention) {
        this.survey = survey;
        this.user = user;
        this.content = content;
        this.parent = parent;
        this.mention = mention;
        this.isDeleted = false;
    }

    public static Comment create(Survey survey, String content, Comment parent, User mention) {
        return new Comment(
                survey,
                new User(UserContextHolder.getUserContext().getUserId()),
                content,
                parent,
                mention
        );
    }

    public String getContent() {
        if(isDeleted) {
            return "삭제된 댓글입니다.";
        }
        return this.content;
    }

    public long likeCount() {
        return responses.stream().filter(r->r.getLike() && !r.isDeleted()).count();
    }

    public long hateCount() {
        return responses.stream().filter(r->!r.getLike() && !r.isDeleted()).count();
    }

    public CommentResponseType responseType() {
        if(UserContextHolder.userId() == null) {
            return CommentResponseType.NONE;
        }
        Optional<CommentResponse> responseOpt = responses.stream()
                .filter(r -> r.getUser().getId().equals(UserContextHolder.userId())).findAny();
        if (responseOpt.isPresent()) {
            if (responseOpt.get().isDeleted()) {
                return CommentResponseType.NONE;
            } else if (responseOpt.get().getLike()) {
                return CommentResponseType.LIKE;
            } else {
                return CommentResponseType.HATE;
            }
        }
        return CommentResponseType.NONE;
    }
}
