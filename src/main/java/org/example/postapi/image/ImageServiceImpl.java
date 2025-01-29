package org.example.postapi.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.image.exception.ImageNotFoundException;
import org.example.postapi.storage.StorageService;
import org.example.postapi.user.entity.AppUser;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.example.postapi.image.ImageConstants.*;
import static org.example.postapi.image.ImageSpecifications.*;

/**
 * @author rival
 * @since 2025-01-28
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService{


    private final StorageService storageService;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    @Override
    @Transactional
    @CachePut(value=IMAGE_CACHE_NAME, key = "#result.id")
    public ImageDto uploadImage(MultipartFile file,String filename, UUID userId) {
        String imageUrl = storageService.getUrl(filename);
        storageService.upload(file, filename);
        Image image = Image.builder()
            .filename(filename)
            .owner(AppUser.builder().id(userId).build())
            .originalName(file.getOriginalFilename())
            .contentType(file.getContentType())
            .size(file.getSize())
            .imageUrl(imageUrl)
            .build();
        imageRepository.save(image);

        return imageMapper.imageToImageDto(image);
    }

    public Page<ImageDto> getImages(UUID userId, Pageable pageable){

        // Non-deleted & Own
        var spec = isNotDeleted().and(ownerEquals(AppUser.builder().id(userId).build()));
        Page<Image> imagePage = imageRepository.findAll(spec, pageable);
        return imageMapper.imagePageToImageDtoPage(imagePage);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value=IMAGE_CACHE_NAME, key="#imageId"),
        @CacheEvict(value=IMAGE_OWNERSHIP_CACHE_NAME,key = "#userId + ':' + #imageId")
    })
    public void deleteImage(Long imageId, UUID userId) {
        Image image = getImageById(imageId);

        image.softDelete();
        imageRepository.save(image);

        String oldKey = image.getFilename();
        String newKey = S3_DELETED_IMAGE_PREFIX + oldKey;
        storageService.rename(oldKey, newKey);
    }

    @Override
    @Transactional
    @Cacheable(value=IMAGE_CACHE_NAME, key="#imageId")
    public ImageDto getImage(Long imageId) {
        Image image = getImageById(imageId);
        return imageMapper.imageToImageDto(image);
    }

    @Override
    @Cacheable(value=IMAGE_OWNERSHIP_CACHE_NAME, key = "#userId + ':' + #imageId")
    public boolean isOwner(Long imageId, UUID userId){
        AppUser owner = AppUser.builder().id(userId).build();
        var spec = isNotDeleted().and(idEquals(imageId)).and(ownerEquals(owner));
        return imageRepository.exists(spec);
    }

    private Image getImageById(Long imageId){
        var spec = isNotDeleted().and(idEquals(imageId));
        return imageRepository.findOne(spec).orElseThrow(() -> new ImageNotFoundException(imageId));
    }

}
