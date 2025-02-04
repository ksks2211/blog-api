package org.example.postapi.domain.post.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rival
 * @since 2025-02-03
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;
    private String content;
    private String description;

    private List<
        @NotBlank(message="Keyword cannot be blank")
        @Pattern(regexp = PostValidationConstants.TAG_RULE_REGEX, message = PostValidationConstants.TAG_RULE_MESSAGE)
        String> tags = new ArrayList<>();
}
