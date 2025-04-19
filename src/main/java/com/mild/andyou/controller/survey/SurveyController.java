package com.mild.andyou.controller.survey;

import com.mild.andyou.application.comment.CommentService;
import com.mild.andyou.application.survey.SurveyService;
import com.mild.andyou.controller.survey.rqrs.*;
import com.mild.andyou.domain.survey.Topic;
import com.mild.andyou.utils.PageResponse;
import com.mild.andyou.utils.PageRq;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final CommentService commentService;

    @PostMapping("/contents")
    public ResponseEntity<ContentSaveRs> saveContent(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(surveyService.saveContent(file));
    }

    @PostMapping
    public ResponseEntity<SurveySaveRs> saveSurvey(@RequestBody SurveySaveRq rq) {
        return ResponseEntity.ok(surveyService.saveSurvey(rq));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SurveySaveRs> updateSurvey(@PathVariable Long id, @RequestBody SurveySaveRq rq) {
        return ResponseEntity.ok(surveyService.updateSurvey(id, rq));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<SurveySearchRs>> getMySurveys(
            @RequestParam @Nullable String keyword,
            PageRq pageRq
    ) {
        return ResponseEntity.ok(PageResponse.from(surveyService.getMySurveys(keyword, pageRq)));
    }

    @GetMapping("/chainable")
    public ResponseEntity<ChainCandidateOptionsRs> chainCandidateOptions(@RequestParam @Nullable Long surveyId) {
        return ResponseEntity.ok(surveyService.chainCandidateOptions(surveyId));
    }

    // ID로 설문 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<SurveyRs> getSurveyById(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveyById(id));
    }

    @GetMapping("/{id}/gpt-opinion")
    public ResponseEntity<GptOpinionRs> getGptOpinion(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getGptOpinion(id));
    }

    // 키워드로 설문 검색
    @GetMapping("/search")
    public ResponseEntity<PageResponse<SurveySearchRs>> searchSurveys(
            @RequestParam @Nullable Topic topic,
            @RequestParam @Nullable String keyword,
            @RequestParam(required = false, defaultValue="NEWEST") SortOrder sortOrder,
            PageRq pageRq) {
        return ResponseEntity.ok(PageResponse.from(surveyService.searchSurveys(topic, keyword, sortOrder, pageRq)));
    }

    // 설문 참여(투표)
    @PostMapping("/{surveyId}/vote")
    public ResponseEntity<SurveyVoteRs> voteSurvey(
            @PathVariable Long surveyId,
            @RequestBody SurveyVoteRq rq) {
        return ResponseEntity.ok(surveyService.voteSurvey(surveyId, rq));
    }

    // 댓글 작성
    @GetMapping("/{surveyId}/comments")
    public ResponseEntity<PageResponse<CommentRs>> getComment(
            @PathVariable Long surveyId, PageRq pageRq) {
        return ResponseEntity.ok(PageResponse.from(commentService.getComments(surveyId, pageRq)));
    }

    // 댓글 작성
    @PostMapping("/{surveyId}/comments")
    public ResponseEntity<CommentRs> addComment(
            @PathVariable Long surveyId,
            @RequestBody CommentAddRq rq) {
        return ResponseEntity.ok(commentService.addComment(surveyId, rq));
    }
} 