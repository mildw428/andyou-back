package com.mild.andyou.domain.survey;
import com.mild.andyou.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name = "survey_responses", uniqueConstraints = {
    @UniqueConstraint(name = "unique_response", columnNames = {"user_id", "survey_id"})
})
@Getter @Setter
@NoArgsConstructor
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private SurveyOption option;

    @CreationTimestamp
    private LocalDateTime respondedAt;

    public SurveyResponse(User user, Survey survey, SurveyOption option) {
        this.user = user;
        this.survey = survey;
        this.option = option;
    }
}
