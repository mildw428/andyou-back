package com.mild.andyou.domain.survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyContentRepository extends JpaRepository<SurveyContent, Long> {
    List<SurveyContent> findAllByFileNameIn(List<String> fileNames);
}
