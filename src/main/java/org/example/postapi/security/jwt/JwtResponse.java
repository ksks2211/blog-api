package org.example.postapi.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author rival
 * @since 2025-01-20
 */

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
}
