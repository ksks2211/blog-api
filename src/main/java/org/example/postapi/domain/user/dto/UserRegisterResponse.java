package org.example.postapi.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author rival
 * @since 2025-01-22
 */

@Data
@AllArgsConstructor
public class UserRegisterResponse {
    private String email;
}
