package org.example.postapi.domain.post.repository;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import static org.example.postapi.common.GlobalConstants.TIMESTAMP_PATTERN_FORMAT;
import static org.example.postapi.common.GlobalConstants.TIMEZONE_FORMAT;


/**
 * @author rival
 * @since 2025-02-03
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostPreviewDto implements Serializable {
    private Long id;
    private UUID authorId;
    private String title;
    private String description;
    private String writerName;


    @JsonFormat(pattern = TIMESTAMP_PATTERN_FORMAT, timezone = TIMEZONE_FORMAT)
    private Instant createdAt;

    @JsonFormat(pattern = TIMESTAMP_PATTERN_FORMAT, timezone = TIMEZONE_FORMAT)
    private Instant updatedAt;

    public PostPreviewDto(Long id, String writerId, String title, String description, String writerName, Instant createdAt, Instant updatedAt){
        this.id = id;
        this.authorId = UUID.fromString(writerId);
        this.title = title;
        this.description = description;
        this.writerName = writerName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
