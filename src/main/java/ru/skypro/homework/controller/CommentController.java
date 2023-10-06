package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.service.CommentService;


@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("ads/{id}/comments")
    public ResponseEntity<Comments> getAllCommentsForAd(Authentication authentication,@PathVariable int id){
        Comments comments =commentService.getAllComments(authentication,id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comments);
    }
    @PostMapping("ads/{id}/comments")
    public ResponseEntity<Comment> createComment(Authentication authentication, @PathVariable int id, @RequestBody CreateOrUpdateComment createOrUpdateComment){
        Comment comment =commentService.createComment(authentication,id,createOrUpdateComment);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comment);
    }
    @DeleteMapping("ads/{adId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(Authentication authentication,@PathVariable(name = "adId") int adId, @PathVariable(name = "commentId") int commentId){
        commentService.deleteComment(authentication,adId, commentId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("ads/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(Authentication authentication,@PathVariable(name = "adId") int adId, @PathVariable(name = "commentId") int commentId, @RequestBody @Valid CreateOrUpdateComment createOrUpdateComment){
        Comment comment =commentService.updateComment(authentication,adId,commentId,createOrUpdateComment);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comment);
    }
}
