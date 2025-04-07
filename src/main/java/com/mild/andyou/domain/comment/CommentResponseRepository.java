package com.mild.andyou.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentResponseRepository extends JpaRepository<CommentResponse, Long> {

    Optional<CommentResponse> findByComment_IdAndUser_Id(Long commentId, Long userId);

}
