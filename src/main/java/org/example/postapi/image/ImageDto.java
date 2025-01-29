package org.example.postapi.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


/**
 * @author rival
 * @since 2025-01-28
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDto {
    private Long id;
    private String filename;
    private String contentType;
    private Long size;
    private String originalName;
    private String imageUrl;
    private String createdAt;
}
