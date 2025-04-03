package com.mild.andyou.application;

import com.mild.andyou.config.filter.UserContextHolder;
import com.mild.andyou.config.properties.BucketProperties;
import com.mild.andyou.controller.survey.rqrs.*;
import com.mild.andyou.domain.survey.*;
import com.mild.andyou.domain.user.User;
import com.mild.andyou.domain.user.UserRepository;
import com.mild.andyou.utils.Delimiter;
import com.mild.andyou.utils.s3.S3FilePath;
import com.mild.andyou.utils.s3.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class SurveyService {

    private final BucketProperties bucketProperties;
    private final SurveyRepository surveyRepository;
    private final SurveyContentRepository surveyContentRepository;
    private final UserRepository userRepository;
    private final SurveyResponseRepository responseRepository;

    @Transactional
    public ContentSaveRs saveContent(MultipartFile file) {

        SurveyContent surveyContent = SurveyContent.create(file.getOriginalFilename());
        surveyContentRepository.save(surveyContent);
        String key = S3FilePath.getSurveyContentKey(surveyContent.getFileName());

        try {
            byte[] bytes = file.getBytes();
            S3Utils.upload(bucketProperties.parse(), key, bytes);
        }catch (Exception e) {
            throw new RuntimeException();
        }

        return new ContentSaveRs(
                Delimiter.SLASH.join(S3FilePath.cdnUrl, S3FilePath.SURVEY_CONTENT_PATH),
                surveyContent.getFileName()
        );
    }

    @Transactional
    public SurveySaveRs saveSurvey(SurveySaveRq rq) {
        Survey survey = Survey.create(rq.getTitle(),
                rq.getDescription(),
                rq.getThumbnail(),
                rq.getContentType(),
                rq.getContent()
        );

        List<SurveyOption> surveyOptions = rq.getOptions().stream().map(o -> SurveyOption.craete(
                o.getText(),
                o.getContentType(),
                o.getContent()
        )).collect(Collectors.toList());

        survey.setOptions(surveyOptions);
        surveyRepository.save(survey);

        List<String> fileNames = rq.getFileNames();
        List<SurveyContent> surveyContents = surveyContentRepository.findAllByFileNameIn(fileNames);
        surveyContents.forEach(c->c.updateStatus(survey));

        return new SurveySaveRs(survey.getId());
    }

    public List<SurveySearchRs> getAllSurveys() {
        List<Survey> surveys = surveyRepository.findAll();
        return surveys.stream()
                .map(SurveySearchRs::convertToSurveyRs)
                .collect(Collectors.toList());
    }

    public List<SurveySearchRs> getMySurveys() {
        if(UserContextHolder.userId() == null) {
            throw new RuntimeException();
        }
        List<Survey> surveys = surveyRepository.findByCreatedBy_Id(UserContextHolder.userId());
        return surveys.stream()
                .map(SurveySearchRs::convertToSurveyRs)
                .collect(Collectors.toList());
    }

    public SurveyRs getSurveyById(Long id) {
        Survey survey = surveyRepository.findById(id).orElseThrow();

        Optional<SurveyResponse> responseOpt = Optional.empty();
        if(UserContextHolder.userId() != null) {
            User user = userRepository.findById(UserContextHolder.userId()).orElseThrow();
            responseOpt = responseRepository.findBySurveyAndUser(survey, user);
        }

        Long selectedId = responseOpt.map(surveyResponse -> surveyResponse.getOption().getId()).orElse(null);
        return SurveyRs.convertToSurveyRs(survey, selectedId);
    }

    public List<SurveySearchRs> searchSurveys(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSurveys();
        }
        
        List<Survey> surveys = surveyRepository.findByTitleContainingIgnoreCase(keyword);
        return surveys.stream()
                .map(SurveySearchRs::convertToSurveyRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public SurveyRs voteSurvey(Long surveyId, SurveyVoteRq rq) {
        Optional<Survey> surveyOpt = surveyRepository.findById(surveyId);
        if (surveyOpt.isEmpty()) {
            return null;
        }
        Survey survey = surveyOpt.get();
        survey.vote(rq.getOptionId());

        return SurveyRs.convertToSurveyRs(survey, rq.getOptionId());
    }


}