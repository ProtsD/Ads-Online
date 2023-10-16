package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ads.Ad;
import ru.skypro.homework.dto.ads.Ads;
import ru.skypro.homework.dto.ads.CreateOrUpdateAd;
import ru.skypro.homework.dto.ads.ExtendedAd;
import ru.skypro.homework.service.AdService;

import javax.validation.Valid;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {
    private final AdService adService;

    @GetMapping()
    public ResponseEntity<Ads> getAllAds() {
        Ads allAds = adService.getAllAds();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(allAds);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(Authentication authentication, @RequestPart @Valid CreateOrUpdateAd properties, @RequestPart MultipartFile image) {
        Ad addAd = adService.addAd(authentication, properties, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(addAd);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAdInfo(Authentication authentication, @PathVariable(name = "id") Integer id) {
        ExtendedAd adInfo = adService.getAdInfo(authentication, id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(adInfo);
    }

    @PreAuthorize("@securityAnnotationMethods.hasPermission(#authentication, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAd(Authentication authentication, @PathVariable(name = "id") Integer id) {
        adService.deleteAd(authentication, id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("@securityAnnotationMethods.hasPermission(#authentication, #id)")
    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAdInfo(Authentication authentication, @PathVariable(name = "id") Integer id, @RequestBody @Valid CreateOrUpdateAd properties) {
        Ad updateAdInfo = adService.updateAdInfo(authentication, id, properties);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(updateAdInfo);
    }

    @GetMapping("/me")
    public ResponseEntity<Ads> getCurrentUserAds(Authentication authentication) {
        Ads currentUserAds = adService.getCurrentUserAds(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(currentUserAds);
    }

    @PreAuthorize("@securityAnnotationMethods.hasPermission(#authentication, #id)")
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAdImage(Authentication authentication, @PathVariable(name = "id") Integer id, @RequestParam MultipartFile image) {
        String updateAdImage = adService.updateAdImage(authentication, id, image);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(updateAdImage);
    }
}
