package org.example.postapi.domain.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.domain.user.entity.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-21
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${auth.refresh-token.exp-hours}")
    private int REFRESH_EXP_HOURS;

    private Instant getExpiresAt(){
        return Instant.now().plus(REFRESH_EXP_HOURS, ChronoUnit.HOURS);
    }

    @Override
    @Transactional
    public String createRefreshToken(UUID userId) {

        AppUser appUser = AppUser.builder().id(userId).build();
        Optional<RefreshToken> optional = refreshTokenRepository.findByUser(appUser);

        RefreshToken refreshToken;
        if(optional.isPresent() && !optional.get().isExpired()){
            refreshToken = optional.get();
        }else{
            // Remove Expired Token
            optional.ifPresent(rt->{
                refreshTokenRepository.deleteById(rt.getId());
                refreshTokenRepository.flush();
            });
            refreshToken = RefreshToken.builder()
                .expiresAt(getExpiresAt())
                .user(appUser).build();
            refreshTokenRepository.save(refreshToken);
        }

        return refreshToken.getId().toString();
    }

    @Override
    @Transactional
    public RefreshToken getRefreshTokenById(UUID tokenId) {
        RefreshToken refreshToken = refreshTokenRepository.findById(tokenId).orElseThrow(() -> new InvalidRefreshTokenException("Invalid token: " + tokenId));
        if(refreshToken.isExpired()){
            refreshTokenRepository.delete(refreshToken);
            throw new ExpiredRefreshTokenException("Expired refresh token: "+tokenId);
        }
        return refreshToken;
    }

    @Override
    @Transactional
    public void deleteRefreshTokenByUserId(UUID userId) {
        refreshTokenRepository.deleteByUser(AppUser.builder().id(userId).build());
    }
}
