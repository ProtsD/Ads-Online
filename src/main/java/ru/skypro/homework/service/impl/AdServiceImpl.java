package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ads.Ad;
import ru.skypro.homework.dto.ads.Ads;
import ru.skypro.homework.dto.ads.CreateOrUpdateAd;
import ru.skypro.homework.dto.ads.ExtendedAd;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.util.ServiceUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserMapper userMapper;
    private final ImageService imageService;
    private final CommentRepository commentRepository;
    private final ServiceUtils serviceUtils;

    @Override
    public Ads getAllAds() {
        List<Ad> adList = StreamSupport.stream(adRepository.findAll().spliterator(), false)
                .map(adMapper::toAd)
                .collect(Collectors.toList());

        return adMapper.toAds(adList);
    }

    @Override
    public Ad addAd(Authentication authentication, CreateOrUpdateAd properties, MultipartFile image) {
        AdEntity currentAd = adMapper.toEntity(properties)
                .setAuthor(userMapper.toEntity(serviceUtils.getCurrentUser(authentication)));

        try {
            byte[] imageBytes = image.getBytes();
            ImageEntity imageEntity = imageService.uploadImage(imageBytes);
            String imageURL = ImageService.IMAGE_URL_PREFIX + imageEntity.getId();
            currentAd.setImage(imageURL);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }

        currentAd = adRepository.save(currentAd);

        return adMapper.toAd(currentAd);
    }

    @Override
    public ExtendedAd getAdInfo(Authentication authentication, Integer id) {
        AdEntity currentAd = adRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Ad with id=" + id + " doesn't found.")
        );

        return adMapper.toExtendedAd(currentAd);
    }

    @Override
    public void deleteAd(Authentication authentication, Integer id) {
        AdEntity currentAd = adRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Ad with id=" + id + " doesn't found.")
        );

        try {
            Integer imageId = Integer.valueOf(currentAd.getImage().replaceAll(ImageService.IMAGE_URL_PREFIX, ""));
            imageService.deleteImage(imageId);
        } catch (NumberFormatException e) {
            log.debug(e.getMessage());
        }
        commentRepository.deleteByAdEntityPk(id);
        adRepository.delete(currentAd);
    }

    @Override
    public Ad updateAdInfo(Authentication authentication, Integer id, CreateOrUpdateAd properties) {
        AdEntity currentAd = adRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Ad with id=" + id + " doesn't found.")
        );

        currentAd.setTitle(properties.getTitle())
                .setPrice(properties.getPrice())
                .setDescription(properties.getDescription());

        currentAd = adRepository.save(currentAd);

        return adMapper.toAd(currentAd);
    }

    @Override
    public Ads getCurrentUserAds(Authentication authentication) {
        int currentUserId = serviceUtils.getCurrentUser(authentication).getId();

        List<Ad> adList = adRepository.findAllByAuthorId(currentUserId)
                .orElseThrow(NotFoundException::new)
                .stream().map(adMapper::toAd)
                .collect(Collectors.toList());

        return adMapper.toAds(adList);
    }

    @Override
    public String updateAdImage(Authentication authentication, Integer id, MultipartFile image) {
        AdEntity currentAd = adRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Ad with id=" + id + " doesn't found.")
        );

        try {
            byte[] imageBytes = image.getBytes();
            Integer imageId = Integer.valueOf(currentAd.getImage().replaceAll(ImageService.IMAGE_URL_PREFIX, ""));
            ImageEntity imageEntity = imageService.updateImage(imageId, imageBytes);
            String imageURL = ImageService.IMAGE_URL_PREFIX + imageEntity.getId();

            currentAd.setImage(imageURL);
        } catch (IOException | NumberFormatException e) {
            log.debug(e.getMessage());
        }

        return currentAd.getImage();
    }
}
