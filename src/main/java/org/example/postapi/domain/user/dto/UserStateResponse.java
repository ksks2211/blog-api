package org.example.postapi.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rival
 * @since 2025-01-31
 */
@Data
@NoArgsConstructor
public class UserStateResponse {

    private boolean isLoggedIn = false;
    private String nickname = "Anonymous";

}
