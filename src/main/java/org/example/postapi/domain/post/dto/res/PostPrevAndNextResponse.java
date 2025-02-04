package org.example.postapi.domain.post.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.postapi.domain.post.repository.PostPreviewDto;

/**
 * @author rival
 * @since 2025-02-03
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostPrevAndNextResponse {


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PostPreviewDto prev;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PostPreviewDto next;



    @JsonProperty("hasPrev")
    public boolean hasPrev(){
        return prev!=null;
    }

    @JsonProperty("hasNext")
    public boolean hasNext(){
        return next!=null;
    }
}
