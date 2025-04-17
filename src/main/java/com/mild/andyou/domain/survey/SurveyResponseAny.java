package com.mild.andyou.domain.survey;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_responses_any")
@Getter @Setter
@NoArgsConstructor
public class SurveyResponseAny {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private SurveyOption option;

    @CreationTimestamp
    private LocalDateTime respondedAt;

    public SurveyResponseAny(String ip, Survey survey, SurveyOption option) {
        this.ip = ip;
        this.survey = survey;
        this.option = option;
    }
}
