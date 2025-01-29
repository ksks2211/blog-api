package org.example.postapi.refresh;

import org.example.postapi.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-21
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID>{


    Optional<RefreshToken> findByUser(AppUser user);

    Optional<RefreshToken> findByIdAndUser(UUID id, AppUser user);

    void deleteByIdAndUser(UUID id, AppUser user);

    @Modifying
    @Query("delete from RefreshToken rt where rt.user = :user")
    void deleteByUser(@Param("user") AppUser user);

    @Modifying
    @Query("delete from RefreshToken rt  where rt.expiresAt < :currentTime and rt.user = :user")
    int deleteExpiredTokenByUser(@Param("currentTime") Instant currentTime, @Param("user") AppUser user);





}
