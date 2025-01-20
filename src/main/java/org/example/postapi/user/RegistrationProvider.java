package org.example.postapi.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author rival
 * @since 2025-01-15
 */


@RequiredArgsConstructor
@Getter
public enum RegistrationProvider {
    LOCAL("local"), GOOGLE("google");
    private final String registrationId;
}
