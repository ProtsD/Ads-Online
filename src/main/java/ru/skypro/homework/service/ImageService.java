package ru.skypro.homework.service;

import ru.skypro.homework.entity.ImageEntity;

public interface ImageService {
    String IMAGE_URL_PREFIX = "/images/";

    /**
     * Returns ImageEntity of the image with the given id.
     *
     * @param id requested image ID
     * @return ImageEntity of requested image
     */
    ImageEntity getImage(Integer id);

    /**
     * Saves a new image
     *
     * @param image image bytes
     * @return ImageEntity of requested image
     */
    ImageEntity uploadImage(byte[] image);

    /**
     * Updates existed image with the given id.
     *
     * @param id    updated image ID
     * @param image image bytes
     * @return ImageEntity of requested image
     */
    ImageEntity updateImage(Integer id, byte[] image);

    /**
     * Deletes the image with the given id.
     *
     * @param id deleted image ID
     */
    void deleteImage(Integer id);
}
