package com.mild.andyou.domain.comment;

import com.mild.andyou.config.filter.UserContextHolder;
import com.mild.andyou.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.function.Supplier;

@Entity
@Table(name = "comment_responses", uniqueConstraints = {
        @UniqueConstraint(name = "unique_comment_response", columnNames = {"user_id", "comment_id"})
})
@Getter
@NoArgsConstructor
public class CommentResponse {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    private Boolean like;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @CreationTimestamp
    private LocalDateTime updatedAt;

    private boolean isDeleted;

    private CommentResponse(Comment comment, User user, Boolean like) {
        this.comment = comment;
        this.user = user;
        this.like = like;
        this.isDeleted=false;
    }

    public static CommentResponse create(Comment comment) {
        return new CommentResponse(
                comment,
                new User(UserContextHolder.userId()),
                true
        );
    }

    public void like() {
        this.like = true;
        this.isDeleted = false;
    }

    public void hate() {
        this.like = false;
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }

}
