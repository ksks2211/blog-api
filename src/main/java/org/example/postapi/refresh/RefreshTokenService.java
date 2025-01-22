package org.example.postapi.refresh;

import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-21
 */
public interface RefreshTokenService {

    String createRefreshToken(UUID userId);

    RefreshToken getRefreshTokenById(UUID tokenId);

    void deleteRefreshTokenByUserId(UUID userId);
}
