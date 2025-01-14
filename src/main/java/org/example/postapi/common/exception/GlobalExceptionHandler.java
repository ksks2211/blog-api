package org.example.postapi.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.postapi.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rival
 * @since 2025-01-14
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.info("Illegal Argument Exception : {}",e.getMessage());

        var body = ApiResponse.error("Bad Request", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> customMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err->{
            String fieldName = ((FieldError) err).getField();
            String errorMessage = err.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.info("Validation Exception : {}",errors);
        return ApiResponse.error("Validation Failed", errors);
    }



    @ExceptionHandler(value= ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> customConstraintViolationException(ConstraintViolationException e){
        Map<String, String> errors = new HashMap<>();
        for (var violation : e.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString(); // 위반된 필드 경로
            String errorMessage = violation.getMessage(); // 유효성 검증 실패 메시지
            errors.put(fieldName, errorMessage);
        }

        log.info("Constraint Violations : {}", errors);
        return ApiResponse.error("Constraint Violated", errors);
    }



}
