package org.example.postapi.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.example.postapi.common.GlobalConstants.TIMESTAMP_PATTERN_FORMAT;
import static org.example.postapi.common.GlobalConstants.TIMEZONE_FORMAT;

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


    @AllArgsConstructor
    @Getter
    public enum Status{
        SUCCESS("success"),ERROR("error");

        @JsonValue
        private final String value;
    }


    private Status status;


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
        @JsonFormat(pattern = TIMESTAMP_PATTERN_FORMAT, timezone = TIMEZONE_FORMAT)
        private final Instant timestamp =  Instant.now();
    }

    // 성공 응답 생성 메서드
    public static <T> ApiResponse<T> success(String message, @NonNull T data) {
        return ApiResponse.<T>builder()
            .status(Status.SUCCESS)
            .message(message)
            .data(data)
            .build();
    }

    public static  ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
            .status(Status.SUCCESS)
            .message(message)
            .build();
    }



    // 에러 응답 생성 메서드
    public static ApiResponse<Void> error(String message, Object... errors) {
        return ApiResponse.<Void>builder()
            .status(Status.ERROR)
            .message(message)
            .errors(Arrays.asList(errors))
            .build();
    }
}
