package org.example.postapi.domain.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author rival
 * @since 2025-02-03
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public Tag createIfNotExists(String tagValue) {
        return tagRepository.findByTagValueIgnoreCase(tagValue)
            .orElseGet(() -> tagRepository.save(new Tag(tagValue)));
    }

    @Override
    @Transactional
    public List<Tag> createTagsIfNotExist(List<String> tagValues) {
        return tagValues.stream().map(this::createIfNotExists).toList();
    }

}
