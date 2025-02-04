package org.example.postapi.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * @author rival
 * @since 2025-01-16
 */
@Getter
@Setter
public abstract class BaseUserRequest {
    @Email
    @NotBlank
    private String email;


    @Size(min=6, max = 50)
    @NotBlank
    private String password;
}
