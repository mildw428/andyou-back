package com.mild.andyou.application.survey;

import com.mild.andyou.application.gpt.GptService;
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
    private final SurveyOptionRepository surveyOptionRepository;
    private final SurveyContentRepository surveyContentRepository;
    private final UserRepository userRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final GptService gptService;

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
                Delimiter.SLASH.join(S3FilePath.CDN_URL, S3FilePath.SURVEY_CONTENT_PATH),
                surveyContent.getFileName()
        );
    }

    @Transactional
    public SurveySaveRs saveSurvey(SurveySaveRq rq) {

        Survey survey = Survey.create(
                rq.getTopic(),
                rq.getType(),
                rq.getTitle(),
                rq.getDescription(),
                rq.getThumbnail().getFileName(),
                rq.getContent().getContentType(),
                rq.getContent().getContentType() == ContentType.YOUTUBE ? rq.getContent().getPath() : rq.getContent().getFileName()
        );

        FeedbackVo incorrectFeedback = rq.getIncorrectFeedback() == null ? null : rq.getIncorrectFeedback().of();

        List<SurveyOption> surveyOptions = rq.getOptions().stream().map(o -> SurveyOption.craete(
                o.getText(),
                o.getContent().getContentType(),
                o.getContent().getContentType() == ContentType.YOUTUBE ? o.getContent().getPath() : o.getContent().getFileName(),
                o.getIsCorrect(),
                o.getIsCorrect() == null ? null : o.getIsCorrect() ? o.getFeedback().of() : incorrectFeedback
        )).collect(Collectors.toList());

        survey.setOptions(surveyOptions);
        surveyRepository.save(survey);

        List<String> fileNames = rq.getFileNames();
        List<SurveyContent> surveyContents = surveyContentRepository.findAllByFileNameIn(fileNames);
        surveyContents.forEach(c->c.updateStatus(survey));

        if(rq.getChainOptionId() != null) {
            SurveyOption surveyOption = surveyOptionRepository.findById(rq.getChainOptionId()).orElseThrow();
            surveyOption.updateChainSurveyId(survey.getId());
        }

        return new SurveySaveRs(survey.getId());
    }

    @Transactional
    public SurveySaveRs updateSurvey(Long id, SurveySaveRq rq) {
        Optional<SurveyOption> chainOptionOpt = surveyOptionRepository.findByChainSurveyId(id);
        chainOptionOpt.ifPresent(SurveyOption::deleteChainSurvey);

        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(id).orElseThrow();
        Optional<SurveyResponse> surveyResponseOpt = surveyResponseRepository.findFirstBySurvey(survey);

        if(!survey.getCreatedBy().getId().equals(UserContextHolder.userId()) || surveyResponseOpt.isPresent()) {
            throw new RuntimeException();
        }

        FeedbackVo incorrectFeedback = rq.getIncorrectFeedback() == null ? null : rq.getIncorrectFeedback().of();

        List<SurveyOption> surveyOptions = rq.getOptions().stream().map(o -> SurveyOption.craete(
                o.getText(),
                o.getContent().getContentType(),
                o.getContent().getContentType() == ContentType.YOUTUBE ? o.getContent().getPath() : o.getContent().getFileName(),
                o.getIsCorrect(),
                o.getIsCorrect() == null ? null : o.getIsCorrect() ? o.getFeedback().of() : incorrectFeedback
        )).collect(Collectors.toList());

        survey.update(
                rq.getTitle(),
                rq.getDescription(),
                rq.getThumbnail().getFileName(),
                rq.getContent().getContentType(),
                rq.getContent().getContentType() == ContentType.YOUTUBE ? rq.getContent().getPath() : rq.getContent().getFileName()
        );
        survey.setOptions(surveyOptions);

        List<String> fileNames = rq.getFileNames();
        List<SurveyContent> surveyContents = surveyContentRepository.findAllByFileNameIn(fileNames);
        surveyContents.forEach(c->c.updateStatus(survey));

        if(rq.getChainOptionId() != null) {
            SurveyOption surveyOption = surveyOptionRepository.findById(rq.getChainOptionId()).orElseThrow();
            surveyOption.updateChainSurveyId(survey.getId());
        }

        return new SurveySaveRs(survey.getId());
    }

    @Transactional
    public void deleteSurvey(Long id) {
        Survey survey = surveyRepository.findById(id).orElseThrow();
        survey.delete();
        Optional<SurveyOption> chainOptionOpt = surveyOptionRepository.findByChainSurveyId(id);
        chainOptionOpt.ifPresent(SurveyOption::deleteChainSurvey);
    }

    public Page<SurveySearchRs> getMySurveys(String keyword, PageRq pageRq) {
        if (UserContextHolder.userId() == null) {
            throw new RuntimeException();
        }
        Page<Survey> surveys = surveyRepository.findByCreatedBy(UserContextHolder.userId(), keyword, pageRq.toPageable());
        Map<Long, Long> loginVoteCountMap = surveyRepository.loginVoteCountMap(surveys.getContent());
        Map<Long, Long> anyVoteCountMap = surveyRepository.anyVoteCountMap(surveys.getContent());

        return surveys.map(s->SurveySearchRs.convertToSurveyRs(
                s, loginVoteCountMap.get(s.getId()) + anyVoteCountMap.get(s.getId())));
    }

    public SurveyRs getSurveyById(Long id) {
        Optional<SurveyOption> chainOptionOpt = surveyOptionRepository.findByChainSurveyId(id);
        Long chainOptionId = chainOptionOpt.map(SurveyOption::getId).orElse(null);

        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(id).orElseThrow();

        Optional<SurveyResponse> responseOpt = Optional.empty();
        if(UserContextHolder.userId() != null) {
            User user = userRepository.findById(UserContextHolder.userId()).orElseThrow();
            responseOpt = surveyResponseRepository.findBySurveyAndUser(survey, user);
        }

        Long selectedId = responseOpt.map(surveyResponse -> surveyResponse.getOption().getId()).orElse(null);
        Map<Long, Long> loginVoteCountMap = surveyRepository.loginVoteCountMap(List.of(survey));
        Map<Long, Long> anyVoteCountMap = surveyRepository.anyVoteCountMap(List.of(survey));
        Long sum = loginVoteCountMap.get(id) + anyVoteCountMap.get(id);

        return SurveyRs.convertToSurveyRs(chainOptionId, survey, selectedId, sum);
    }

    @Transactional
    public GptOpinionRs getGptOpinion(Long id) {
        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(id).orElseThrow();
        String opinion = survey.getGptOpinion();
        if(opinion == null || opinion.isBlank()) {
            opinion = gptService.createOpinion(survey);
        }
        survey.updateGptOpinion(opinion);

        return new GptOpinionRs(opinion);
    }

    public Page<SurveySearchRs> searchSurveys(Topic topic, String keyword, SortOrder order, PageRq pageRq) {

        Page<Survey> surveys = surveyRepository.findBySearch(topic, keyword, order, pageRq.toPageable());
        Map<Long, Long> loginVoteCountMap = surveyRepository.loginVoteCountMap(surveys.getContent());
        Map<Long, Long> anyVoteCountMap = surveyRepository.anyVoteCountMap(surveys.getContent());

        return surveys.map(s->SurveySearchRs.convertToSurveyRs(
                s, loginVoteCountMap.get(s.getId()) + anyVoteCountMap.get(s.getId())));
    }

    @Transactional
    public SurveyVoteRs voteSurvey(Long surveyId, SurveyVoteRq rq) {
        Optional<Survey> surveyOpt = surveyRepository.findByIdAndIsDeletedFalse(surveyId);
        if (surveyOpt.isEmpty()) {
            return null;
        }
        Survey survey = surveyOpt.get();
        survey.vote(rq.getOptionId());
        Map<Long, Long> loginVoteCountMap = surveyRepository.loginVoteCountMap(List.of(survey));
        Map<Long, Long> anyVoteCountMap = surveyRepository.anyVoteCountMap(List.of(survey));
        Long sum = loginVoteCountMap.get(surveyId) + anyVoteCountMap.get(surveyId);

        survey.updateVoteCount(sum.intValue());
        return SurveyVoteRs.convertToSurveyRs(survey, rq.getOptionId(), sum);
    }

    public ChainCandidateOptionsRs chainCandidateOptions(Long id) {
        List<Survey> surveys = surveyRepository.findChainCandidateSurvey(id);
        return ChainCandidateOptionsRs.create(surveys, id);
    }
}