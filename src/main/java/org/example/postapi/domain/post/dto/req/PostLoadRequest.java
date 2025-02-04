package org.example.postapi.domain.post.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rival
 * @since 2025-02-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLoadRequest {


    @Min(0)
    private Long postId;

    @Pattern(regexp = "^newer$|^older$", message = "load = newer or older")
    private String load;


    public boolean isRequested(){
        return load != null && postId !=null;
    }
}
