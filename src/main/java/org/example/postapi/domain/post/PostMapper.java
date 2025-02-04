package org.example.postapi.domain.post;

import org.example.postapi.common.BaseMapper;
import org.example.postapi.domain.post.dto.req.PostCreateRequest;
import org.example.postapi.domain.post.dto.res.PostDetailResponse;
import org.example.postapi.domain.post.repository.PostPreviewDto;
import org.example.postapi.domain.tag.Tag;
import org.example.postapi.domain.user.entity.AppUser;
import org.example.postapi.security.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-02-03
 */
@Mapper(componentModel = "spring")
public interface PostMapper extends BaseMapper {




    @Mappings(
        {
            @Mapping(source = "tags", target = "postTags",ignore = true)
        }
    )
    Post postCreateReqToPost(PostCreateRequest request);



    @Mappings(
        {
            @Mapping(target = "tags", ignore = true),
            @Mapping(target = "authorId", ignore = true),

        }
    )
    PostDetailResponse postToPostDetail(Post post);


    @Mappings(
        {
            @Mapping(target = "authorId", source = "author", qualifiedByName = "extractId")
        }
    )
    PostPreviewDto postToPostPreview(Post post);


    @Named("extractId")
    default UUID toISODateTime(AppUser appUser) {
        return appUser.getId();
    }


    default Post postCreateReqToPost(PostCreateRequest request, List<Tag> tags, AuthUser user) {
        Post post = postCreateReqToPost(request);

        tags.forEach(post::addTag);
        post.setAuthor(AppUser.builder().id(user.getId()).build());
        post.setWriterId(user.getId().toString());
        post.setWriterName(user.getNickname());

        return post;
    }

    default PostDetailResponse postToPostDetail(Post post, List<Tag> tags, UUID authorId) {

        PostDetailResponse response = postToPostDetail(post);
        response.setAuthorId(authorId);
        List<String> tagValues = response.getTags();
        tags.forEach(t->tagValues.add(t.getTagValue()));

        return response;
    }
}
