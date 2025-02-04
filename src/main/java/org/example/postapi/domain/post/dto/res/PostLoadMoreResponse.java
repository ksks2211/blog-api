package org.example.postapi.domain.post.dto.res;

import lombok.*;
import org.example.postapi.domain.post.repository.PostPreviewDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rival
 * @since 2025-02-04
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLoadMoreResponse {

    private Long targetPostId;
    private String load;
    private List<PostPreviewDto> postList = new ArrayList<>();


    public boolean isEmpty(){
        return postList.isEmpty();
    }

}
