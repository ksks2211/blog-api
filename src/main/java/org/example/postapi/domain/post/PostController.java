package org.example.postapi.domain.post;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.common.dto.ApiResponse;
import org.example.postapi.common.resolver.PageRequestDefault;
import org.example.postapi.domain.post.dto.req.PostCreateRequest;
import org.example.postapi.domain.post.dto.req.PostLoadRequest;
import org.example.postapi.domain.post.dto.res.PostDetailResponse;
import org.example.postapi.domain.post.dto.res.PostLoadMoreResponse;
import org.example.postapi.domain.post.dto.res.PostPageResponse;
import org.example.postapi.security.AuthUser;
import org.example.postapi.security.resolver.CurrentUserUUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.example.postapi.domain.post.PostConstants.POST_URL_PREFIX;

/**
 * @author rival
 * @since 2025-02-04
 */

@RestController
@RequestMapping(POST_URL_PREFIX)
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> createPost(@AuthenticationPrincipal AuthUser user, @RequestBody @Valid PostCreateRequest createRequest){
        PostDetailResponse post = postService.createPost(createRequest, user);
        return ApiResponse.success("New Post created", post);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_POST_PRIVILEGE')")
    public ApiResponse<?> getPost(@PathVariable Long id){
        PostDetailResponse post = postService.findPostById(id);
        return ApiResponse.success("Post found", post);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() && @postServiceImpl.isOwner(#id, #userId)")
    public ApiResponse<?> deletePost(@PathVariable Long id, @CurrentUserUUID UUID userId){
        postService.deletePost(id);
        log.info("User({}) deleted post({})",userId, id);
        return ApiResponse.success("Post deleted");
    }


    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() && @postServiceImpl.isOwner(#id, #user.id)")
    public ApiResponse<?> updatePost(@PathVariable Long id, @RequestBody @Valid PostCreateRequest createRequest, @AuthenticationPrincipal AuthUser user){
        PostDetailResponse post = postService.updatePost(id, createRequest, user);
        return ApiResponse.success("Post updated", post);
    }

    @GetMapping("/prev-and-next")
    public ApiResponse<?> getPrevAndNextPost(@RequestParam(value="postId") Long postId){
        var prevAndNext = postService.findPrevAndNext(postId);
        return ApiResponse.success("previous post and next post", prevAndNext);
    }




    @GetMapping("")
    public ApiResponse<?> getPosts(@PageRequestDefault Pageable pageable, @Valid PostLoadRequest postLoadRequest){



        if(postLoadRequest != null && postLoadRequest.isRequested()){
            String loadType = postLoadRequest.getLoad();
            Long postId = postLoadRequest.getPostId();
            PostLoadMoreResponse loadMoreResponse = postService.loadMorePosts(pageable, postId, loadType);


            return ApiResponse.success("Post load more", loadMoreResponse);
        }else{
            PostPageResponse posts = postService.findPosts(pageable);

            return ApiResponse.success("Post page", posts);
        }


    }




}
