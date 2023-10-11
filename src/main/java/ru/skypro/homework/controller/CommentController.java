package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

import javax.validation.Valid;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getAllCommentsForAd(Authentication authentication, @PathVariable Integer id) {
        Comments comments = commentService.getAllComments(authentication, id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comments);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> createComment(Authentication authentication, @PathVariable Integer id, @RequestBody @Valid CreateOrUpdateComment createOrUpdateComment) {
        Comment comment = commentService.createComment(authentication, id, createOrUpdateComment);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comment);
    }

    @PreAuthorize("@securityAnnotationMethods.hasPermission(#authentication, #adId, #commentId)")
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(Authentication authentication, @PathVariable(name = "adId") Integer adId, @PathVariable(name = "commentId") Integer commentId) {
        commentService.deleteComment(authentication, adId, commentId);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@securityAnnotationMethods.hasPermission(#authentication, #adId, #commentId)")
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(Authentication authentication, @PathVariable(name = "adId") Integer adId, @PathVariable(name = "commentId") Integer commentId, @RequestBody @Valid CreateOrUpdateComment createOrUpdateComment) {
        Comment comment = commentService.updateComment(authentication, adId, commentId, createOrUpdateComment);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comment);
    }
}
