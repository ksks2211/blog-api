package org.example.postapi.domain.user;

import org.example.postapi.domain.user.entity.AppUser;

import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-15
 */
public interface AppUserService {
    void createAppUser(String email, String password);
    AppUser createOrUpdateAppUser(RegistrationProvider provider, String providedId, String providedName, String providedEmail);
    AppUser getAppUserByEmail(String email);
    AppUser getAppUserById(UUID id);
}
