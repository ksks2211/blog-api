package org.example.postapi.domain.postag;

import org.example.postapi.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author rival
 * @since 2025-02-03
 */
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @Query("select pt.tag.tagValue from PostTag pt where pt.post = :post")
    List<String> findAllTagValuesByPost(Post post);
}
