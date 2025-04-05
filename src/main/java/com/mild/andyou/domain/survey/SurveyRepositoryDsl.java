package com.mild.andyou.domain.survey;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface SurveyRepositoryDsl {

    Page<Survey> findBySearch(String keyword, Pageable pageable);

    Page<Survey> findByCreatedBy(Long userId, Pageable pageable);

    Map<Long, Long> countMap(List<Survey> surveys);


}
