package org.example.postapi.domain.tag;

import java.util.List;

/**
 * @author rival
 * @since 2025-02-03
 */
public interface TagService {
    Tag createIfNotExists(String tagValue);

    List<Tag> createTagsIfNotExist(List<String> tagValues);
}
