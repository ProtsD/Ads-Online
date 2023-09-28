package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

import javax.validation.Valid;


@RestController("/ads")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getAllComments(Authentication authentication,@PathVariable int id){
        Comments comments =commentService.getAllComments(authentication,id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comments);
    }
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> createComment(Authentication authentication, @PathVariable int id, @RequestBody @Valid CreateOrUpdateComment createOrUpdateComment){
        Comment comment =commentService.createComment(authentication,id,createOrUpdateComment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comment);
    }
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(Authentication authentication,@PathVariable int adId, @PathVariable int commentId){
        commentService.deleteComment(authentication,adId, commentId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(Authentication authentication,@PathVariable int adId, @PathVariable int commentId, @RequestBody @Valid CreateOrUpdateComment createOrUpdateComment){
        Comment comment =commentService.updateComment(authentication,adId,commentId,createOrUpdateComment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comment);
    }
}
