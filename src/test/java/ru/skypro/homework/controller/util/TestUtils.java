package ru.skypro.homework.controller.util;

import com.github.javafaker.Faker;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.user.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.security.SecurityUserPrincipal;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {
    static final int usernameMinSize = 6, usernameMaxSize = 32;
    static final int passwordMinSize = 8, passwordMaxSize = 16;
    static final int firstNameMinSize = 2, firstNameMaxSize = 16;
    static final int lastNameMinSize = 2, lastNameMaxSize = 16;
    static final Faker ruFaker = new Faker(new Locale("ru-RU"));
    static final Faker enFaker = new Faker(new Locale("en-US"));

    public static List<UserEntity> createUniqueUsers(int maxUsers, PasswordEncoder passwordEncoder) {
        List<UserEntity> users = new ArrayList<>();

        List<String> uniqueEmails = Stream
                .generate(() -> enFaker.internet().emailAddress())
                .distinct()
                .filter(email -> email.length() >= usernameMinSize && email.length() <= usernameMaxSize)
                .limit(maxUsers)
                .collect(Collectors.toList());

        List<String> passwords = getPasswords(maxUsers);
        List<String> firstNames = getFirstNames(maxUsers);
        List<String> lastNames = getLastNames(maxUsers);
        List<String> uniquePhones = getPhones(maxUsers);

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

    public static List<String> getPasswords(int Quantity) {
        return Stream.generate(() -> ruFaker.internet().password())
                .filter(password -> password.length() >= passwordMinSize && password.length() <= passwordMaxSize)
                .limit(Quantity)
                .collect(Collectors.toList());
    }

    public static List<String> getFirstNames(int Quantity) {
        return Stream.generate(() -> ruFaker.name().firstName())
                .filter(firstName -> firstName.length() >= firstNameMinSize && firstName.length() <= firstNameMaxSize)
                .limit(Quantity)
                .collect(Collectors.toList());
    }

    public static List<String> getLastNames(int Quantity) {
        return Stream.generate(() -> ruFaker.name().lastName())
                .filter(lastName -> lastName.length() >= lastNameMinSize && lastName.length() <= lastNameMaxSize)
                .limit(Quantity)
                .collect(Collectors.toList());
    }

    public static List<String> getPhones(int Quantity) {
        return Stream.generate(() -> ruFaker.phoneNumber().phoneNumber())
                .distinct()
                .limit(Quantity)
                .collect(Collectors.toList());
    }
    public static UserEntity createAdmin(List<UserEntity> users) {
        int randomUser = new Random().nextInt(users.size());
        users.get(randomUser).setRole(Role.ADMIN);


        return users.get(randomUser);
    }

    public static List<AdEntity> createAds(int maxAds, List<UserEntity> users, ImageService imageService) throws IOException {
        final int priceMinValue = 0, priceMaxValue = 10000000;
        final int titleMinSize = 4, titleMaxSize = 32;
        final int descriptionMinSize = 8, descriptionMaxSize = 64;
        final Faker ruFaker = new Faker(new Locale("ru-RU"));

        List<AdEntity> ads = new ArrayList<>();

        for (int currentAdNumber = 0; currentAdNumber < maxAds; currentAdNumber++) {
            String adTitle = Stream
                    .generate(() -> ruFaker.commerce().productName())
                    .filter(title -> title.length() >= titleMinSize && title.length() <= titleMaxSize)
                    .limit(1)
                    .collect(Collectors.joining());

            String adDescription = Stream
                    .generate(() -> ruFaker.commerce().productName())
                    .filter(desc -> desc.length() >= descriptionMinSize)
                    .distinct()
                    .limit(10)
                    .collect(Collectors.joining(", "));

            adDescription = adDescription.substring(0, Math.min(adDescription.length(), descriptionMaxSize));


            MockMultipartFile imageFile = new MockMultipartFile("file", "file1.png", MediaType.IMAGE_PNG_VALUE, "mockPseudoValue".getBytes());
            ImageEntity imageEntity = imageService.uploadImage(imageFile.getBytes());
            String uploadedImage = ImageService.IMAGE_URL_PREFIX + imageEntity.getId();

            AdEntity currentAd = new AdEntity()
                    .setPrice(new Random().nextInt(priceMaxValue))
                    .setTitle(adTitle)
                    .setDescription(adDescription)
                    .setImage(uploadedImage)
                    .setAuthor(users.get(new Random().nextInt(users.size())));

            ads.add(currentAd);
        }

        return ads;
    }

    public static List<CommentEntity> createComments(int maxComments, List<UserEntity> users, List<AdEntity> ads) {
        final int textMinSize = 8, textMaxSize = 64;
        final Faker ruFaker = new Faker(new Locale("ru-RU"));
        List<CommentEntity> comments = new ArrayList<>();

        for (AdEntity currentAd : ads) {
            int totalCommentsForCurrentAd = new Random().nextInt(maxComments);

            for (int currentCommentNumber = 0; currentCommentNumber <= totalCommentsForCurrentAd; currentCommentNumber++) {
                String text = Stream
                        .generate(() -> ruFaker.commerce().productName())
                        .filter(desc -> desc.length() >= textMinSize)
                        .distinct()
                        .limit(10)
                        .collect(Collectors.joining(", "));

                text = text.substring(0, Math.min(text.length(), textMaxSize));

                int randomUserNumber = new Random().nextInt(users.size());

                long createAt;
                if (comments.isEmpty()) {
                    createAt = new Date().getTime();
                } else {
                    createAt = comments.get(comments.size() - 1).getCreatedAt() + new Random().nextInt(1000);
                }

                CommentEntity currentComment = new CommentEntity()
                        .setCreatedAt(createAt)
                        .setText(text)
                        .setAdEntity(currentAd)
                        .setAuthor(users.get(randomUserNumber));

                comments.add(currentComment);
            }
        }
        return comments;
    }

    public static Authentication createAuthenticationTokenForUser(UserEntity user) {
        if (user == null) return null;

        UserMapper userMapper = new UserMapper();

        SecurityUserPrincipal securityUserPrincipal = new SecurityUserPrincipal(userMapper.toFullUserInfo(user));

        return new UsernamePasswordAuthenticationToken(
                securityUserPrincipal,
                null,
                securityUserPrincipal.getAuthorities()
        );
    }

    public static Authentication createAuthenticationTokenForRandomUser(List<UserEntity> users) {
        return createAuthenticationTokenForUser(users.get(new Random().nextInt(users.size())));
    }
    public static CommentEntity getRandomExistedComment(List<CommentEntity> commentList){
        return commentList.get(new Random().nextInt(commentList.size()));
    }
    public static UserEntity getRandomUser(List<UserEntity> users){
        return users.get(new Random().nextInt(users.size()));
    }

    public static AdEntity getRandomExistedAd(List<AdEntity> ads) {
        return ads.get(new Random().nextInt(ads.size()));
    }

    public static UserEntity getRandomUserFrom(List<UserEntity> users) {
        return users.get(new Random().nextInt(users.size()));
    }

    public static int getRandomNonExistentAdId(List<AdEntity> ads) {
        int randomNonExistentId;
        List<Integer> allIds = ads.stream().map(AdEntity::getPk).collect(Collectors.toList());

        do {
            randomNonExistentId = new Random().nextInt(Integer.MAX_VALUE);
        } while (allIds.contains(randomNonExistentId));

        return randomNonExistentId;
    }
}
