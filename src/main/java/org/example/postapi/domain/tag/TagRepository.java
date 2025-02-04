package org.example.postapi.domain.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author rival
 * @since 2025-02-03
 */
public interface TagRepository extends JpaRepository<Tag,Long> {
    Optional<Tag> findByTagValueIgnoreCase(String tagValue);

}
