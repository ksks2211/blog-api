package org.example.postapi.domain.post.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.postapi.domain.post.Post;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author rival
 * @since 2025-02-03
 */
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> , PostSearchRepository{



    @Query("SELECT p FROM Post p WHERE p.deleted = false and p.id = :id")
    @NotNull
    Optional<Post> findById(@Param("id") @NotNull Long id);

    @NotNull
    @Query("SELECT new org.example.postapi.domain.post.repository.PostPreviewDto(p.id, p.author.id, p.title, p.description,p.writerName, p.createdAt, p.updatedAt) FROM Post p WHERE p.deleted = false")
    Page<PostPreviewDto> findAllPreview(Pageable pageable);


    @Query("select new org.example.postapi.domain.post.repository.PostPreviewDto(p.id, p.author.id, p.title, p.description,p.writerName, p.createdAt, p.updatedAt) " +
        "from Post p " +
        "where p.id > :id and p.deleted = false " +
        "order by p.id asc limit 1")
    Optional<PostPreviewDto> findNextPost(@Param("id") Long id);

    @Query("select new org.example.postapi.domain.post.repository.PostPreviewDto(p.id, p.author.id, p.title, p.description,p.writerName, p.createdAt, p.updatedAt) " +
        "from Post p " +
        "where p.id < :id and p.deleted = false " +
        "order by p.id desc limit 1")
    Optional<PostPreviewDto> findPrevPost(@Param("id") Long id);



    @Modifying
    @Query("update Post p set p.deleted = true, p.deletedAt = instant where p.id = :id")
    void softDelete(@Param("id") Long id);

}
