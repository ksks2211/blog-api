package org.example.postapi.domain.post.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.postapi.domain.post.repository.PostPreviewDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rival
 * @since 2025-02-03
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostPageResponse {
    private Integer totalPages;
    private List<PostPreviewDto> postList = new ArrayList<>();
}
