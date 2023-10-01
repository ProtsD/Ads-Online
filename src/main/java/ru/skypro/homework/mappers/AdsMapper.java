package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.ads.Ad;
import ru.skypro.homework.dto.ads.Ads;
import ru.skypro.homework.dto.ads.CreateOrUpdateAd;
import ru.skypro.homework.dto.ads.ExtendedAd;
import ru.skypro.homework.entity.AdsEntity;

import java.util.List;

@Component
public class AdsMapper {

    public AdsEntity toEntity(CreateOrUpdateAd createOrUpdateAd){
        if(createOrUpdateAd == null){
            return null;
        }

        AdsEntity adsEntity = new AdsEntity();
        adsEntity.setPrice(createOrUpdateAd.getPrice());
        adsEntity.setTitle(createOrUpdateAd.getTitle());
        adsEntity.setDescription(createOrUpdateAd.getDescription());

        return adsEntity;
    }

    public Ad toAd(AdsEntity adsEntity){
        if(adsEntity == null){
            return null;
        }

        Ad ad = new Ad();
        ad.setAuthor(adsEntity.getAuthor().getId());
        ad.setImage(adsEntity.getImage());
        ad.setPk(adsEntity.getPk());
        ad.setPrice(adsEntity.getPrice());
        ad.setTitle(adsEntity.getTitle());

        return ad;
    }

    public ExtendedAd toExtendedAd(AdsEntity adsEntity){
        if(adsEntity == null){
            return null;
        }

        ExtendedAd extendedAd = new ExtendedAd();
        extendedAd.setPk(adsEntity.getPk());
        extendedAd.setAuthorFirstName(adsEntity.getAuthor().getFirstName());
        extendedAd.setAuthorLastName(adsEntity.getAuthor().getLastName());
        extendedAd.setDescription(adsEntity.getDescription());
        extendedAd.setEmail(adsEntity.getAuthor().getEmail());
        extendedAd.setImage(adsEntity.getImage());
        extendedAd.setPhone(adsEntity.getAuthor().getPhone());
        extendedAd.setPrice(adsEntity.getPrice());
        extendedAd.setTitle(adsEntity.getTitle());

        return extendedAd;
    }

    public Ads toAds(List<Ad> adList){
        if (adList == null){
            return null;
        }

        Ads ads = new Ads();
        ads.setCount(adList.size());
        ads.setResults(adList);

        return ads;
    }
}
