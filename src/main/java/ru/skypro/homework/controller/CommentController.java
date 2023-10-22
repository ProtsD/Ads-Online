package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Получение комментариев объявления", tags = {"Комментарии"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Comments.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")}
    )
    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getAllCommentsForAd(Authentication authentication, @PathVariable Integer id) {
        Comments comments = commentService.getAllComments(authentication, id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comments);
    }

    @Operation(summary = "Добавление комментария к объявлению", tags = {"Комментарии"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")}
    )
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> createComment(Authentication authentication, @PathVariable Integer id, @RequestBody @Valid CreateOrUpdateComment createOrUpdateComment) {
        Comment comment = commentService.createComment(authentication, id, createOrUpdateComment);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comment);
    }

    @Operation(summary = "Удаление комментария", tags = {"Комментарии"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")}
    )
    @PreAuthorize("@securityAnnotationMethods.hasPermission(#authentication, #adId, #commentId)")
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(Authentication authentication, @PathVariable(name = "adId") Integer adId, @PathVariable(name = "commentId") Integer commentId) {
        commentService.deleteComment(authentication, adId, commentId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обновление комментария", tags = {"Комментарии"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")}
    )
    @PreAuthorize("@securityAnnotationMethods.hasPermission(#authentication, #adId, #commentId)")
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(Authentication authentication, @PathVariable(name = "adId") Integer adId, @PathVariable(name = "commentId") Integer commentId, @RequestBody @Valid CreateOrUpdateComment createOrUpdateComment) {
        Comment comment = commentService.updateComment(authentication, adId, commentId, createOrUpdateComment);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(comment);
    }
}
