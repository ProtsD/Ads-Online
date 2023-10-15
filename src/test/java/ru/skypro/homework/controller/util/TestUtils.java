package ru.skypro.homework.controller.util;

import com.github.javafaker.Faker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.user.FullUserInfo;
import ru.skypro.homework.dto.user.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.security.SecurityUserPrincipal;
import ru.skypro.homework.service.ImageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {
    public static List<UserEntity> createUniqueUsers(int maxUsers, PasswordEncoder passwordEncoder) {
        final int usernameMinSize = 6, usernameMaxSize = 32;
        final int passwordMinSize = 8, passwordMaxSize = 16;
        final int firstNameMinSize = 2, firstNameMaxSize = 16;
        final int lastNameMinSize = 2, lastNameMaxSize = 16;
        final Faker ruFaker = new Faker(new Locale("ru-RU"));
        final Faker enFaker = new Faker(new Locale("en-US"));

        List<UserEntity> users = new ArrayList<>();

        List<String> uniqueEmails = Stream
                .generate(
                        () -> enFaker.internet().emailAddress()
                )
                .distinct()
                .filter(
                        email -> email.length() >= usernameMinSize && email.length() <= usernameMaxSize
                )
                .limit(maxUsers)
                .collect(Collectors.toList());

        List<String> passwords = Stream
                .generate(
                        () -> ruFaker.internet().password()
                )
                .filter(
                        password -> password.length() >= passwordMinSize && password.length() <= passwordMaxSize
                )
                .limit(maxUsers)
                .collect(Collectors.toList());

        List<String> firstNames = Stream
                .generate(
                        () -> ruFaker.name().firstName()
                )
                .filter(
                        firstName -> firstName.length() >= firstNameMinSize && firstName.length() <= firstNameMaxSize
                )
                .limit(maxUsers)
                .collect(Collectors.toList());

        List<String> lastNames = Stream
                .generate(
                        () -> ruFaker.name().lastName()
                )
                .filter(
                        lastName -> lastName.length() >= lastNameMinSize && lastName.length() <= lastNameMaxSize
                )
                .limit(maxUsers)
                .collect(Collectors.toList());

        List<String> uniquePhones = Stream
                .generate(
                        () -> ruFaker.phoneNumber().phoneNumber()
                )
                .distinct()
                .limit(maxUsers)
                .collect(Collectors.toList());

        for (int currentUserNumber = 0; currentUserNumber < maxUsers; currentUserNumber++) {
            UserEntity currentUser = new UserEntity()
                    .setUsername(uniqueEmails.get(currentUserNumber))
                    .setPassword(passwordEncoder.encode(passwords.get(currentUserNumber)))
                    .setFirstName(firstNames.get(currentUserNumber))
                    .setLastName(lastNames.get(currentUserNumber))
                    .setPhone(uniquePhones.get(currentUserNumber))
                    .setRole(Role.USER)
                    .setImage(null);

            users.add(currentUser);
        }

        return users;
    }

    public static UserEntity createAdmin(List<UserEntity> users) {
        int randomUser = new Random().nextInt(users.size());
        users.get(randomUser).setRole(Role.ADMIN);

        return users.get(randomUser);
    }

    public static List<AdEntity> createAds(int maxAds, List<UserEntity> users, ImageService imageService) {
        final int priceMinValue = 0, priceMaxValue = 10000000;
        final int titleMinSize = 4, titleMaxSize = 32;
        final int descriptionMinSize = 8, descriptionMaxSize = 64;
        final Faker ruFaker = new Faker(new Locale("ru-RU"));

        List<AdEntity> ads = new ArrayList<>();

        for (int currentAdNumber = 0; currentAdNumber < maxAds; currentAdNumber++) {
            String adTitle = Stream
                    .generate(
                            () -> ruFaker.commerce().productName()
                    )
                    .filter(
                            title -> title.length() >= titleMinSize && title.length() <= titleMaxSize
                    ).limit(1).collect(Collectors.joining());

            //TODO
            String image = "STUB";
            AdEntity currentAd = new AdEntity()
                    .setPrice(new Random().nextInt(priceMaxValue))
                    .setTitle(adTitle)
                    .setDescription("1111111111")
                    .setImage(image)
                    .setAuthor(users.get(new Random().nextInt(users.size())));
            ads.add(currentAd);
        }

        return ads;
    }

    public static Authentication createAuthenticationTokenForUser(UserEntity user) {
        if (user == null) return null;

        UserMapper userMapper = new UserMapper();
        FullUserInfo fullUserInfo = userMapper.toFullUserInfo(user);
        SecurityUserPrincipal securityUserPrincipal = new SecurityUserPrincipal(fullUserInfo);

        return new UsernamePasswordAuthenticationToken(
                securityUserPrincipal,
                null,
                securityUserPrincipal.getAuthorities()
        );
    }
}
