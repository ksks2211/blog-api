package org.example.postapi.domain.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author rival
 * @since 2025-01-28
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDto implements Serializable {
    private Long id;
    private String filename;
    private String contentType;
    private Long size;
    private String originalName;
    private String imageUrl;
    private String createdAt;
}
