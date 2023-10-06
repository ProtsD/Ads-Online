package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ads.Ad;
import ru.skypro.homework.dto.ads.Ads;
import ru.skypro.homework.dto.ads.CreateOrUpdateAd;
import ru.skypro.homework.dto.ads.ExtendedAd;

public interface AdService {
    Ads getAllAds();

    Ad addAd(Authentication authentication, CreateOrUpdateAd properties, MultipartFile image);

    ExtendedAd getAdInfo(Authentication authentication, Integer id);

    void deleteAd(Authentication authentication, Integer id);

    Ad updateAdInfo(Authentication authentication, Integer id, CreateOrUpdateAd properties);

    Ads getCurrentUserAds(Authentication authentication);

    String updateAdImage(Authentication authentication, Integer id, MultipartFile image);
}
