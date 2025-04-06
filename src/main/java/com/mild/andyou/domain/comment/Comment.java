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

    private Boolean deleted = false;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Comment> children = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Comment(Survey survey, User user, String content, Comment parent, User mention) {
        this.survey = survey;
        this.user = user;
        this.content = content;
        this.parent = parent;
        this.mention = mention;
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
}
