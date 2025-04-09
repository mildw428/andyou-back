package com.mild.andyou.domain.survey;

import com.mild.andyou.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyRepositoryDsl {

    Optional<Survey> findByIdAndIsDeletedFalse(Long id);
}
