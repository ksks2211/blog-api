package org.example.postapi.domain.user;

import org.example.postapi.domain.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-15
 */
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    boolean existsByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByRegistrationProviderAndProvidedId(RegistrationProvider registrationProvider, String providedId);
}
