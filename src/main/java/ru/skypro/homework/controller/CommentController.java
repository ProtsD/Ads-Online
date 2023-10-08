package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.service.CommentService;


@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getAllCommentsForAd(Authentication authentication,@PathVariable int id){
        Comments comments =commentService.getAllComments(authentication,id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comments);
    }
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> createComment(Authentication authentication, @PathVariable int id, @RequestBody CreateOrUpdateComment createOrUpdateComment){
        Comment comment =commentService.createComment(authentication,id,createOrUpdateComment);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comment);
    }
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(Authentication authentication,@PathVariable(name = "adId") int adId, @PathVariable(name = "commentId") int commentId){
        commentService.deleteComment(authentication,adId, commentId);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(Authentication authentication,@PathVariable(name = "adId") int adId, @PathVariable(name = "commentId") int commentId, @RequestBody @Valid CreateOrUpdateComment createOrUpdateComment){
        Comment comment =commentService.updateComment(authentication,adId,commentId,createOrUpdateComment);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comment);
    }
}
