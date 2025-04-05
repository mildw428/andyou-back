package com.mild.andyou.domain.survey;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SurveyRepositoryDsl {

    Page<Survey> findBySearch(String keyword, Pageable pageable);

    Page<Survey> findByCreatedBy(Long userId, Pageable pageable);


}
