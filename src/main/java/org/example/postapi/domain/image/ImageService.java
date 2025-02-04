package org.example.postapi.domain.image;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-28
 */
public interface ImageService {

    ImageDto uploadImage(MultipartFile file,String filename, UUID userId);


    void deleteImage(Long imageId, UUID userId);

    ImageDto getImage(Long imageId);

    boolean isOwner(Long imageId, UUID userId);
}
