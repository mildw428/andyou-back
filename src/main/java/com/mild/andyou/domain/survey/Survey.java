package com.mild.andyou.domain.survey;

import com.mild.andyou.config.filter.UserContextHolder;
import com.mild.andyou.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "surveys")
@Getter
@NoArgsConstructor
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Topic topic;

    @Enumerated(EnumType.STRING)
    private SurveyType type;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 500)
    private String description;

    private String gptOpinion;

    private Integer voteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Boolean isDeleted;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SurveyOption> options = new ArrayList<>();

    public Survey(Topic topic, SurveyType type, String title, String description, User createdBy) {
        this.topic = topic;
        this.type = type;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.isDeleted = false;
        this.voteCount = 0;

    }

    public static Survey create(Topic topic, SurveyType type, String title, String description) {
        User user = new User(UserContextHolder.userId());
        return new Survey(topic, type, title, description, user);
    }

    public void update(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void updateGptOpinion(String opinion) {
        this.gptOpinion = opinion;
    }

    public void updateVoteCount(Integer count) {
        this.voteCount = count;
    }

    public Optional<SurveyOption> getOption(Long optionId) {
        return options.stream()
                .filter(option -> option.getId().equals(optionId))
                .findFirst();
    }

    public Boolean vote(Long optionId) {

        Optional<SurveyOption> optionOpt = getOption(optionId);

        if (optionOpt.isEmpty()) {
            return false;
        }
        if(UserContextHolder.userId() != null) {
            optionOpt.get().vote();
        }else {
            optionOpt.get().voteAny();
        }
        return true;
    }

    private void addOption(SurveyOption surveyOption) {
        surveyOption.setSurvey(this);
        this.options.add(surveyOption);
    }

    public void setOptions(List<SurveyOption> surveyOptions) {
        if(this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.clear();
        surveyOptions.forEach(this::addOption);
    }

    public void delete() {
        this.isDeleted=true;
    }

}
