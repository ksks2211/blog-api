package org.example.postapi.domain.post.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class PostSearchQueryRequest {


    private UUID writerId;

    private List<
        @NotBlank(message="Tag cannot be blank")
        String> tags = new ArrayList<>();


}
