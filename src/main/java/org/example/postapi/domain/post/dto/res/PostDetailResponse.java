package org.example.postapi.domain.post.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.postapi.domain.post.repository.PostPreviewDto;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-02-03
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse extends PostPreviewDto implements Serializable {



    private String content;
    private List<String> tags = new ArrayList<>();



    // Category
}
