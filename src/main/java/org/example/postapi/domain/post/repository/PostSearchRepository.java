package org.example.postapi.domain.post.repository;

import org.example.postapi.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * @author rival
 * @since 2025-02-03
 */
public interface PostSearchRepository {
    Page<PostPreviewDto> searchPostPage(Specification<Post> spec, Pageable pageable);
    List<PostPreviewDto> searchPostList(Specification<Post> spec, Pageable pageable);
}
