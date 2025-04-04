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

    private String text;

    @Embedded
    private ContentVo contentVo;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SurveyResponse> responses = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public SurveyOption(String text, ContentVo contentVo) {
        this.text = text;
        this.contentVo = contentVo;
    }

    public static SurveyOption craete(String text, ContentType contentType, String contentUrl) {
        return new SurveyOption(text, new ContentVo(contentType, contentUrl));
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

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }
}
