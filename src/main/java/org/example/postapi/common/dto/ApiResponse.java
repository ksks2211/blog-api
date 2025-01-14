package org.example.postapi.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * @author rival
 * @since 2025-01-14
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ApiResponse<T> {


    // "success", "error"
    private String status;


    private String message;

    @Builder.Default
    private Meta meta = new Meta();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Object> errors;



    // Metadata class
    @Getter
    @NoArgsConstructor
    @ToString
    public static class Meta{

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
        private final Instant timestamp =  Instant.now();
    }

    // 성공 응답 생성 메서드
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .status("success")
            .message(message)
            .data(data)
            .build();
    }

    // 에러 응답 생성 메서드
    public static ApiResponse<Void> error(String message, Object... errors) {
        return ApiResponse.<Void>builder()
            .status("error")
            .message(message)
            .errors(Arrays.asList(errors))
            .build();
    }
}
