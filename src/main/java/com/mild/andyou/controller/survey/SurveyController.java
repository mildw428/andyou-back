package com.mild.andyou.controller.survey;

import com.mild.andyou.application.CommentService;
import com.mild.andyou.application.SurveyService;
import com.mild.andyou.controller.survey.rqrs.*;
import lombok.RequiredArgsConstructor;
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

    // 모든 설문 조회
    @GetMapping
    public ResponseEntity<List<SurveySearchRs>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurveys());
    }

    // ID로 설문 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<SurveyRs> getSurveyById(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveyById(id));
    }

    // 키워드로 설문 검색
    @GetMapping("/search")
    public ResponseEntity<List<SurveySearchRs>> searchSurveys(@RequestParam String keyword) {
        return ResponseEntity.ok(surveyService.searchSurveys(keyword));
    }

    // 설문 참여(투표)
    @PostMapping("/{surveyId}/vote")
    public ResponseEntity<SurveyRs> voteSurvey(
            @PathVariable Long surveyId,
            @RequestBody SurveyVoteRq rq) {
        return ResponseEntity.ok(surveyService.voteSurvey(surveyId, rq));
    }

    // 댓글 작성
    @GetMapping("/{surveyId}/comments")
    public ResponseEntity<List<CommentRs>> getComment(
            @PathVariable Long surveyId) {
        return ResponseEntity.ok(commentService.getComments(surveyId));
    }

    // 댓글 작성
    @PostMapping("/{surveyId}/comments")
    public ResponseEntity<CommentRs> addComment(
            @PathVariable Long surveyId,
            @RequestBody CommentAddRq rq) {
        return ResponseEntity.ok(commentService.addComment(surveyId, rq.getContent()));
    }
} 