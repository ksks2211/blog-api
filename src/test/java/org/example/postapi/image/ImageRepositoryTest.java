package org.example.postapi.image;

import org.example.postapi.common.config.JpaConfig;
import org.example.postapi.user.AppUserRepository;
import org.example.postapi.user.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author rival
 * @since 2025-01-29
 */


@DataJpaTest
@Import(JpaConfig.class)
class ImageRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private TestEntityManager entityManager;


    AppUser create_user(){
        String email = "example123@gmail.com";
        String password = "password";
        AppUser user = AppUser.builder().email(email).password(password).build();
        AppUser createdUser = appUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        return createdUser;
    }


    Image create_image(AppUser owner){
        String originalName = "original";
        String filename = "image.png";
        Long size = 1024L;
        String contentType = "image/png";
        Image image = Image.builder()
            .owner(owner)
            .originalName(originalName)
            .filename(filename)
            .size(size)
            .contentType(contentType)
            .build();

        imageRepository.save(image);
        entityManager.flush();
        entityManager.clear();
        return image;
    }


    @Test
    void test_create_image(){
        // Given
        var owner = create_user();
        String originalName = "original";
        String filename = "image.png";
        Long size = 1024L;
        String contentType = "image/png";
        Image image = Image.builder()
            .owner(owner)
            .originalName(originalName)
            .filename(filename)
            .size(size)
            .contentType(contentType)
            .build();
        Image savedImage = imageRepository.save(image);
        Long id = savedImage.getId();


        // When
        Optional<Image> optional = imageRepository.findById(id);

        assert(optional.isPresent());
        Image foundImage = optional.get();
        assertEquals(id, foundImage.getId());
        assertEquals(originalName, foundImage.getOriginalName());
        assertEquals(filename, foundImage.getFilename());
        assertEquals(size, foundImage.getSize());

    }


    @Test
    void find_image_with_spec(){
        var owner = create_user();
        Image image = create_image(owner);

        List<Image> imageList = imageRepository.findAll(ImageSpecifications.isNotDeleted().and(ImageSpecifications.ownerEquals(owner)));

        assertEquals(1, imageList.size());
        Image foundImage = imageList.get(0);

        assertEquals(image.getFilename(), foundImage.getFilename());
        assertEquals(image.getOriginalName(), foundImage.getOriginalName());
    }
}