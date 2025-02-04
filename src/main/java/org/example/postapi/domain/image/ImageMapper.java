package org.example.postapi.domain.image;
import org.example.postapi.common.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * @author rival
 * @since 2025-01-28
 */

@Mapper(componentModel = "spring")
public interface ImageMapper extends BaseMapper {


    @Mapping(target="createdAt", source = "createdAt", qualifiedByName = "toISODateTime")
    ImageDto imageToImageDto(Image image);


    List<ImageDto> imagesToImageDtoList(List<Image> images);


    default Page<ImageDto> imagePageToImageDtoPage(Page<Image> images) {
        List<ImageDto> imageDtoList = imagesToImageDtoList(images.toList());
        return new PageImpl<>(imageDtoList, images.getPageable(), images.getTotalElements());
    }
}
