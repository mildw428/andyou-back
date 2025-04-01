package com.mild.andyou.domain.survey;

import com.mild.andyou.utils.FileExtension;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "survey_contents")
@Getter
@NoArgsConstructor
public class SurveyContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String originName;

    private String fileName;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private SurveyContent(Status status, String originName, String fileName) {
        this.status = status;
        this.originName = originName;
        this.fileName = fileName;
    }
    public static SurveyContent create(String originName) {
        String uuid = UUID.randomUUID().toString();
        FileExtension fileExtension = FileExtension.getFileExtension(originName);
        return new SurveyContent(Status.TEMP, originName, uuid+fileExtension.getValue());
    }

    public void updateStatus(Survey survey) {
        this.survey = survey;
        this.status = Status.REGISTERED;
    }
}
enum Status {
    TEMP,
    REGISTERED
}
