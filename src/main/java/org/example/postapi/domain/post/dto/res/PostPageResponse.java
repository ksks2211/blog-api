package org.example.postapi.domain.post.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.postapi.domain.post.repository.PostPreviewDto;

import java.io.Serializable;
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
public class PostPageResponse implements Serializable {
    private int totalPages;
    private int pageNumber; // current page
    private long totalElements;
    private List<PostPreviewDto> postList = new ArrayList<>();

    public boolean isLastPage() {
        return totalPages==0 || pageNumber == totalPages;
    }

    public boolean isFirstPage() {
        return  totalPages!= 0 || pageNumber == 1 ;
    }

    public boolean isHasPrevPage() {
        return pageNumber > 1;
    }

    public boolean isHasNextPage() {
        return pageNumber < totalPages;
    }
}
