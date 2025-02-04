package org.example.postapi.domain.post;

import org.example.postapi.domain.post.dto.req.PostCreateRequest;
import org.example.postapi.domain.post.dto.req.PostSearchQueryRequest;
import org.example.postapi.domain.post.dto.res.PostDetailResponse;
import org.example.postapi.domain.post.dto.res.PostLoadMoreResponse;
import org.example.postapi.domain.post.dto.res.PostPageResponse;
import org.example.postapi.domain.post.dto.res.PostPrevAndNextResponse;
import org.example.postapi.security.AuthUser;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * @author rival
 * @since 2025-02-03
 */
public interface PostService {


    PostDetailResponse createPost(PostCreateRequest postCreateRequest, AuthUser user);

    PostDetailResponse findPostById(Long postId);
    PostDetailResponse updatePost(Long postId, PostCreateRequest postCreateRequest, AuthUser user);


    PostPageResponse findPosts(Pageable pageable);
    PostPageResponse findPosts(Pageable pageable, UUID userId);

    PostLoadMoreResponse loadMorePosts(Pageable pageable, Long postId, String loadType);
    PostPageResponse searchPosts(Pageable pageable, PostSearchQueryRequest postSearchQueryRequest);

    PostPrevAndNextResponse findPrevAndNext(Long id);

    void deletePost(Long postId);

    boolean isOwner(Long postId, UUID userId);


}
