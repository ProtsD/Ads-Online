package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.service.ImageService;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @Operation(summary = "Получение изображения", tags = {"Изображения"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = MediaType.IMAGE_PNG_VALUE),
                    @Content(mediaType = MediaType.IMAGE_JPEG_VALUE),
                    @Content(mediaType = MediaType.IMAGE_GIF_VALUE)}),
            @ApiResponse(responseCode = "404", description = "Not found")}
    )
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable(name = "id") Integer id) {
        byte[] image = imageService.getImage(id).getImage();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE)
                .body(image);
    }
}
