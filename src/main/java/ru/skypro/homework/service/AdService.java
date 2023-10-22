package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ads.Ad;
import ru.skypro.homework.dto.ads.Ads;
import ru.skypro.homework.dto.ads.CreateOrUpdateAd;
import ru.skypro.homework.dto.ads.ExtendedAd;

public interface AdService {
    /**
     * Returns list of all ads and it's amount.
     *
     * @return all existed ads
     */
    Ads getAllAds();

    /**
     * Saves a new ad
     *
     * @param authentication the currently authenticated principal, or an authentication request token.
     * @param properties     properties for new ad
     * @param image          MultipartFile for image
     * @return information about created ad
     */
    Ad addAd(Authentication authentication, CreateOrUpdateAd properties, MultipartFile image);

    /**
     * Returns extended info of the ad with the given id.
     *
     * @param authentication the currently authenticated principal, or an authentication request token.
     * @param id             requested ad ID
     * @return extended information about requested ad
     * @throws ru.skypro.homework.exception.NotFoundException     if no value is found
     */
    ExtendedAd getAdInfo(Authentication authentication, Integer id);

    /**
     * Deletes the ad with the given id.
     *
     * @param authentication the currently authenticated principal, or an authentication request token.
     * @param id             deleted ad ID
     * @throws ru.skypro.homework.exception.ForbiddenException if user has no access to the current ad
     * @throws ru.skypro.homework.exception.NotFoundException  if no value is found
     */
    void deleteAd(Authentication authentication, Integer id);

    /**
     * Updates the ad with the given id.
     *
     * @param authentication the currently authenticated principal, or an authentication request token.
     * @param id             updated ad ID
     * @param properties     new properties for existed ad
     * @return information about updated ad
     * @throws ru.skypro.homework.exception.ForbiddenException if user has no access to the current ad
     * @throws ru.skypro.homework.exception.NotFoundException  if no value is found
     */
    Ad updateAdInfo(Authentication authentication, Integer id, CreateOrUpdateAd properties);

    /**
     * Returns amount and list of current user ads with the given id.
     *
     * @param authentication the currently authenticated principal, or an authentication request token.
     * @return amount and list of current user ads
     */
    Ads getCurrentUserAds(Authentication authentication);

    /**
     * Updates image of the ad with the given id.
     *
     * @param authentication the currently authenticated principal, or an authentication request token.
     * @param id             updated image ID
     * @param image          MultipartFile for image
     * @return image URL
     * @throws ru.skypro.homework.exception.ForbiddenException if user has no access to the current ad
     * @throws ru.skypro.homework.exception.NotFoundException  if no value is found
     */
    String updateAdImage(Authentication authentication, Integer id, MultipartFile image);
}
