package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ads.Ad;
import ru.skypro.homework.dto.ads.Ads;
import ru.skypro.homework.dto.ads.CreateOrUpdateAd;
import ru.skypro.homework.dto.ads.ExtendedAd;
import ru.skypro.homework.dto.user.Role;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.service.AdService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserMapper userMapper;

    @Override
    public Ads getAllAds() {
        List<Ad> adList = StreamSupport.stream(adRepository.findAll().spliterator(), false)
                .map(adMapper::toAd)
                .collect(Collectors.toList());

        return adMapper.toAds(adList);
    }

    @Override
    public Ad addAd(Authentication authentication, CreateOrUpdateAd properties, MultipartFile image) {
        String imageURL = "TEMPORARY_STUB";

        AdEntity currentAd = adMapper.toEntity(properties)
                .setImage(imageURL)
                .setAuthor(userMapper.toEntity(getCurrentUser(authentication)));
        //TODO image creation from MultipartFile

        currentAd = adRepository.save(currentAd);

        return adMapper.toAd(currentAd);
    }

    @Override
    public ExtendedAd getAdInfo(Authentication authentication, Integer id) {
        AdEntity currentAd = adRepository.findById(id).orElseThrow(
                () -> new NotFoundException("")
        );

        return adMapper.toExtendedAd(currentAd);
    }

    @Override
    public void deleteAd(Authentication authentication, Integer id) {
        AdEntity currentAd = adRepository.findById(id).orElseThrow(
                () -> new NotFoundException("")
        );

        if (checkPermission(authentication, currentAd)) {
            adRepository.delete(currentAd);
        }
    }

    @Override
    public Ad updateAdInfo(Authentication authentication, Integer id, CreateOrUpdateAd properties) {
        AdEntity currentAd = adRepository.findById(id).orElseThrow(
                () -> new NotFoundException("")
        );

        if (checkPermission(authentication, currentAd)) {
            currentAd.setTitle(properties.getTitle())
                    .setPrice(properties.getPrice())
                    .setDescription(properties.getDescription());

            currentAd = adRepository.save(currentAd);
        }

        return adMapper.toAd(currentAd);
    }

    @Override
    public Ads getCurrentUserAds(Authentication authentication) {
        int currentUserId = getCurrentUser(authentication).getId();

        List<Ad> adList = adRepository.findAllByAuthorId(currentUserId)
                .orElseThrow(
                        () -> new NotFoundException("")
                )
                .stream().map(adMapper::toAd)
                .collect(Collectors.toList());

        return adMapper.toAds(adList);
    }

    @Override
    public String updateAdImage(Authentication authentication, Integer id, MultipartFile image) {
        AdEntity currentAd = adRepository.findById(id).orElseThrow(
                () -> new NotFoundException("")
        );

        String imageURL = "TEMPORARY_STUB";

        if (checkPermission(authentication, currentAd)) {
            //TODO image update from MultipartFile
            currentAd.setImage(imageURL);
        }

        return currentAd.getImage();
    }

    private boolean checkPermission(Authentication authentication, AdEntity adEntity) {
        if (getCurrentUser(authentication).getId() == adEntity.getAuthor().getId() || getCurrentUser(authentication).getRole().equals(Role.ADMIN)) {
            return true;
        } else {
            throw new ForbiddenException("");
        }
    }

    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
