package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ads.Ad;
import ru.skypro.homework.dto.ads.Ads;
import ru.skypro.homework.dto.ads.CreateOrUpdateAd;
import ru.skypro.homework.dto.ads.ExtendedAd;
import ru.skypro.homework.service.AdsService;

@Service
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {
    @Override
    public Ads getAllAds(Authentication authentication) {
        return null;
    }

    @Override
    public Ad addAd(Authentication authentication, CreateOrUpdateAd properties, MultipartFile image) {
        return null;
    }

    @Override
    public ExtendedAd getAdInfo(Authentication authentication, Integer id) {
        return null;
    }

    @Override
    public void deleteAd(Authentication authentication, Integer id) {

    }

    @Override
    public Ad updateAdInfo(Authentication authentication, Integer id, CreateOrUpdateAd properties) {
        return null;
    }

    @Override
    public Ads getCurrentUserAds(Authentication authentication) {
        return null;
    }

    @Override
    public String updateAdImage(Authentication authentication, Integer id, MultipartFile image) {
        return null;
    }
}
