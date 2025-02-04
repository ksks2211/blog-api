package org.example.postapi.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.postapi.domain.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-02-03
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchQuery {

    private UUID writerId;
    private List<Tag> tags = new ArrayList<>();

}
