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

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String thumbnail;

    @Embedded
    private ContentVo contentVo;

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

    public Survey(Topic topic, String title, String description, String thumbnail, ContentVo contentVo, User createdBy) {
        this.topic = topic;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.contentVo = contentVo;
        this.createdBy = createdBy;
        this.isDeleted = false;
    }

    public static Survey create(Topic topic, String title, String description, String thumbnailUrl, ContentType contentType, String contentUrl) {
        User user = new User(UserContextHolder.userId());
        return new Survey(topic, title, description, thumbnailUrl, new ContentVo(contentType, contentUrl), user);
    }

    public void update(String title, String description, String thumbnail, ContentType contentType, String content) {
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.contentVo = new ContentVo(contentType, content);
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

        optionOpt.get().vote();
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
}
