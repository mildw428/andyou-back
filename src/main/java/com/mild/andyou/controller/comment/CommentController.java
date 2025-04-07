package com.mild.andyou.controller.comment;

import com.mild.andyou.application.comment.CommentService;
import com.mild.andyou.controller.comment.rqrs.CommentResponseRq;
import com.mild.andyou.controller.comment.rqrs.CommentResponseRs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PatchMapping("/{id}/response")
    public ResponseEntity<CommentResponseRs> commentResponse(@PathVariable Long id, @RequestBody CommentResponseRq rq) {
        return ResponseEntity.ok(commentService.commentResponse(id, rq));
    }

}
