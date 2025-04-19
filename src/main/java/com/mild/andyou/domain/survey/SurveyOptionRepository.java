package com.mild.andyou.domain.survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyOptionRepository extends JpaRepository<SurveyOption, Long> {
    Optional<SurveyOption> findByChainSurveyId(Long id);
}
