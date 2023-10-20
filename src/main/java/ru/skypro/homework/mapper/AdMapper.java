package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.ads.Ad;
import ru.skypro.homework.dto.ads.Ads;
import ru.skypro.homework.dto.ads.CreateOrUpdateAd;
import ru.skypro.homework.dto.ads.ExtendedAd;
import ru.skypro.homework.entity.AdEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdMapper {

    public AdEntity toEntity(CreateOrUpdateAd createOrUpdateAd) {
        if (createOrUpdateAd == null) {
            return null;
        }

        return new AdEntity()
                .setPrice(createOrUpdateAd.getPrice())
                .setTitle(createOrUpdateAd.getTitle())
                .setDescription(createOrUpdateAd.getDescription());
    }

    public Ad toAd(AdEntity adEntity) {
        if (adEntity == null) {
            return null;
        }

        return new Ad()
                .setAuthor(adEntity.getAuthor().getId())
                .setImage(adEntity.getImage())
                .setPk(adEntity.getPk())
                .setPrice(adEntity.getPrice())
                .setTitle(adEntity.getTitle());
    }

    public List<Ad> toAdList(List<AdEntity> adEntities) {
        if (adEntities == null) {
            return Collections.emptyList();
        }
        return adEntities.stream().map(this::toAd).collect(Collectors.toList());
    }

    public ExtendedAd toExtendedAd(AdEntity adEntity) {
        if (adEntity == null) {
            return null;
        }

        return new ExtendedAd()
                .setPk(adEntity.getPk())
                .setAuthorFirstName(adEntity.getAuthor().getFirstName())
                .setAuthorLastName(adEntity.getAuthor().getLastName())
                .setDescription(adEntity.getDescription())
                .setEmail(adEntity.getAuthor().getUsername())
                .setImage(adEntity.getImage())
                .setPhone(adEntity.getAuthor().getPhone())
                .setPrice(adEntity.getPrice())
                .setTitle(adEntity.getTitle());
    }

    public Ads toAds(List<Ad> adList) {
        if (adList == null) {
            return null;
        }

        Ads ads = new Ads();
        ads.setCount(adList.size());
        ads.setResults(adList);

        return ads;
    }
}
