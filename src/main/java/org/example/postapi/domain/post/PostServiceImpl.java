package org.example.postapi.domain.post;


import lombok.RequiredArgsConstructor;
import org.example.postapi.common.resolver.PageRequestDefault;
import org.example.postapi.domain.post.dto.req.PostCreateRequest;
import org.example.postapi.domain.post.dto.req.PostSearchQueryRequest;
import org.example.postapi.domain.post.dto.res.PostDetailResponse;
import org.example.postapi.domain.post.dto.res.PostLoadMoreResponse;
import org.example.postapi.domain.post.dto.res.PostPageResponse;
import org.example.postapi.domain.post.dto.res.PostPrevAndNextResponse;
import org.example.postapi.domain.post.repository.PostPreviewDto;
import org.example.postapi.domain.post.repository.PostRepository;
import org.example.postapi.domain.post.repository.PostSpecification;
import org.example.postapi.domain.postag.PostTag;
import org.example.postapi.domain.tag.Tag;
import org.example.postapi.domain.tag.TagService;
import org.example.postapi.domain.user.entity.AppUser;
import org.example.postapi.security.AuthUser;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.example.postapi.common.GlobalConstants.SHORT_LIVE_CACHE_NAME;
import static org.example.postapi.domain.post.PostConstants.POST_CACHE_NANE;

/**
 * @author rival
 * @since 2025-02-03
 */

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final TagService tagService;

    @Override
    @Transactional
    @CachePut(value=POST_CACHE_NANE, key = "#result.id")
    public PostDetailResponse createPost(PostCreateRequest postCreateRequest, AuthUser user) {
        var tagValues = postCreateRequest.getTags();
        List<Tag> tags = tagService.createTagsIfNotExist(tagValues);
        Post post = postMapper.postCreateReqToPost(postCreateRequest, tags, user);
        post = postRepository.save(post);

        return postMapper.postToPostDetail(post, tags, user.getId());
    }

    @Override
    @Transactional
    @Cacheable(value=POST_CACHE_NANE, key="#postId")
    public PostDetailResponse findPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        List<Tag> tags = post.getPostTags().stream().map(PostTag::getTag).toList();
        UUID authorId = UUID.fromString(post.getWriterId());
        return postMapper.postToPostDetail(post,tags,authorId);
    }

    @Override
    @Transactional
    @CachePut(value=POST_CACHE_NANE, key="#postId")
    public PostDetailResponse updatePost(Long postId, PostCreateRequest postCreateRequest, AuthUser user) {

        var tagValues = postCreateRequest.getTags();
        List<Tag> tags = tagService.createTagsIfNotExist(tagValues);
        Post post = postMapper.postCreateReqToPost(postCreateRequest, tags, user);
        post.setId(postId);
        post = postRepository.save(post);

        return postMapper.postToPostDetail(post, tags, user.getId());
    }

    @Override
    public PostPageResponse findPosts(@PageRequestDefault Pageable pageable) {
        Page<PostPreviewDto> page = postRepository.searchPostPage(PostSpecification.isNotDeleted(), pageable);
        return postMapper.postPageToPostPageResponse(page);
    }

    @Override
    public PostPageResponse findPosts(Pageable pageable, UUID userId) {
        return null;
    }


    @Override
    public PostLoadMoreResponse loadMorePosts(Pageable pageable, Long postId, String loadType) {
        var notDeleted = PostSpecification.isNotDeleted();
        var prevOrNext = loadType.equals("newer") ? PostSpecification.isNext(postId) : PostSpecification.isPrev(postId);
        Sort sort = loadType.equals("newer") ? Sort.by(Post_.ID).ascending() : Sort.by(Post_.ID).descending();


        PageRequest pageRequest = PageRequest.of(0, pageable.getPageSize(), sort);

        List<PostPreviewDto> postList = postRepository.searchPostList(Specification.allOf(notDeleted, prevOrNext), pageRequest);


        return PostLoadMoreResponse.builder()
            .postList(postList)
            .targetPostId(postId)
            .load(loadType)
            .build();
    }

    @Override
    public PostPageResponse searchPosts(Pageable pageable, PostSearchQueryRequest postSearchQueryRequest) {
        return null;
    }

    @Override
    public PostPrevAndNextResponse findPrevAndNext(Long id) {
        PostPrevAndNextResponse prevAndNext = new PostPrevAndNextResponse();
        postRepository.findNextPost(id).ifPresent(
            prevAndNext::setNext);
        postRepository.findPrevPost(id).ifPresent(
            prevAndNext::setPrev);
        return prevAndNext;
    }

    @Override
    @CacheEvict(value=POST_CACHE_NANE, key="#postId")
    @Transactional
    public void deletePost(Long postId) {
        postRepository.softDelete(postId);
    }

    @Override
    @Cacheable(value=SHORT_LIVE_CACHE_NAME, key = "'post:own'+ #postId + ':' + #userId")
    @Transactional
    public boolean isOwner(Long postId, UUID userId) {

        var notDeleted = PostSpecification.isNotDeleted();
        var postIdMatch = PostSpecification.idEquals(postId);
        var userIdMatch = PostSpecification.wroteBy(AppUser.builder().id(userId).build());

        var spec = Specification.allOf(notDeleted, postIdMatch, userIdMatch);

        return postRepository.exists(spec);
    }
}
