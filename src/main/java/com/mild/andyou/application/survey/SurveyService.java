package com.mild.andyou.application.survey;

import com.mild.andyou.config.filter.UserContextHolder;
import com.mild.andyou.config.properties.BucketProperties;
import com.mild.andyou.controller.survey.rqrs.*;
import com.mild.andyou.domain.survey.*;
import com.mild.andyou.domain.user.User;
import com.mild.andyou.domain.user.UserRepository;
import com.mild.andyou.utils.Delimiter;
import com.mild.andyou.utils.PageRq;
import com.mild.andyou.utils.s3.S3FilePath;
import com.mild.andyou.utils.s3.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final SurveyResponseRepository surveyResponseRepository;

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

    @Transactional
    public SurveySaveRs updateSurvey(Long id, SurveySaveRq rq) {
        Survey survey = surveyRepository.findById(id).orElseThrow();
        Optional<SurveyResponse> surveyResponseOpt = surveyResponseRepository.findFirstBySurvey(survey);

        if(!survey.getCreatedBy().getId().equals(UserContextHolder.userId()) || surveyResponseOpt.isPresent()) {
            throw new RuntimeException();
        }

        List<SurveyOption> surveyOptions = rq.getOptions().stream().map(o -> SurveyOption.craete(
                o.getText(),
                o.getContentType(),
                o.getContent()
        )).collect(Collectors.toList());

        survey.update(
                rq.getTitle(),
                rq.getDescription(),
                rq.getThumbnail(),
                rq.getContentType(),
                rq.getContent()
        );
        survey.setOptions(surveyOptions);

        List<String> fileNames = rq.getFileNames();
        List<SurveyContent> surveyContents = surveyContentRepository.findAllByFileNameIn(fileNames);
        surveyContents.forEach(c->c.updateStatus(survey));

        return new SurveySaveRs(survey.getId());
    }

    public Page<SurveySearchRs> getMySurveys(PageRq pageRq) {
        if (UserContextHolder.userId() == null) {
            throw new RuntimeException();
        }
        Page<Survey> surveys = surveyRepository.findByCreatedBy(UserContextHolder.userId(), pageRq.toPageable());
        Map<Long, Long> countMap = surveyRepository.countMap(surveys.getContent());

        return surveys.map(s-> SurveySearchRs.convertToSurveyRs(s, countMap.get(s.getId())));
    }

    public SurveyRs getSurveyById(Long id) {
        Survey survey = surveyRepository.findById(id).orElseThrow();

        Optional<SurveyResponse> responseOpt = Optional.empty();
        if(UserContextHolder.userId() != null) {
            User user = userRepository.findById(UserContextHolder.userId()).orElseThrow();
            responseOpt = surveyResponseRepository.findBySurveyAndUser(survey, user);
        }

        Long selectedId = responseOpt.map(surveyResponse -> surveyResponse.getOption().getId()).orElse(null);
        return SurveyRs.convertToSurveyRs(survey, selectedId);
    }

    public Page<SurveySearchRs> searchSurveys(String keyword, PageRq pageRq) {

        Page<Survey> surveys = surveyRepository.findBySearch(keyword, pageRq.toPageable());
        Map<Long, Long> countMap = surveyRepository.countMap(surveys.getContent());

        return surveys.map(s->SurveySearchRs.convertToSurveyRs(s, countMap.get(s.getId())));
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