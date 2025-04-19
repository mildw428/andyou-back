package com.mild.andyou.domain.survey;
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
@Table(name = "survey_options")
@Getter
@NoArgsConstructor
public class SurveyOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Column(length = 500)
    private String text;

    @Embedded
    private ContentVo contentVo;

    private Boolean isCorrect;

    @Embedded
    private FeedbackVo feedback;

    private Long chainSurveyId;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SurveyResponse> responses = new ArrayList<>();

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SurveyResponseAny> responsesAny = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public SurveyOption(String text, ContentVo contentVo, Boolean isCorrect, FeedbackVo feedback) {
        this.text = text;
        this.contentVo = contentVo;
        this.isCorrect = isCorrect;
        this.feedback = feedback;
    }

    public static SurveyOption craete(String text, ContentType contentType, String contentUrl,
                                      Boolean isCorrect, FeedbackVo feedback) {
        return new SurveyOption(text, ContentVo.create(contentType, contentUrl), isCorrect, feedback);
    }

    public void vote() {
        // 투표 기록 생성
        SurveyResponse response = new SurveyResponse(
                new User(UserContextHolder.getUserContext().getUserId()),
                this.survey,
                this
        );
        responses.add(response);
    }

    public void voteAny() {
        // 투표 기록 생성
        SurveyResponseAny response = new SurveyResponseAny(
                UserContextHolder.getUserContext().getIp(),
                this.survey,
                this
        );
        responsesAny.add(response);
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public void updateChainSurveyId(Long id) {
        this.chainSurveyId = id;
    }
    public void deleteChainSurvey() {
        this.chainSurveyId = null;
    }
}
