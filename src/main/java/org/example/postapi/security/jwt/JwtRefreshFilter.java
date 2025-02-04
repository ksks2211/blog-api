package org.example.postapi.security.jwt;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.domain.refresh.RefreshCookieService;
import org.example.postapi.domain.refresh.RefreshTokenService;
import org.example.postapi.security.AuthUser;
import org.example.postapi.security.AuthUserService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static org.example.postapi.common.util.ExceptionUtils.toAuthenticationException;


/**
 * @author rival
 * @since 2025-01-21
 */
@Slf4j
@RequiredArgsConstructor
public class JwtRefreshFilter extends OncePerRequestFilter {



    private final static String HTTP_METHOD = "POST";


    private final String targetUrl;

    private final RefreshCookieService refreshCookieService;
    private final RefreshTokenService refreshTokenService;
    private final AuthUserService authUserService;


    // handlers
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;






    private boolean isRefreshRequest(HttpServletRequest request){
        return targetUrl.equals(request.getRequestURI())&&request.getMethod().equalsIgnoreCase(HTTP_METHOD);
    }


    private String extractRefreshToken(HttpServletRequest request){
        Cookie cookie = refreshCookieService.extractRefreshCookie(request.getCookies())
            .orElseThrow(() -> new AuthenticationServiceException("Refresh Token is not found."));
        return cookie.getValue();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        boolean isRefreshRequest = isRefreshRequest(request);
        if(isRefreshRequest){
            log.info("Refresh Filter....");
            handleRefreshToken(request, response);
        }else{
            filterChain.doFilter(request, response);
        }
    }


    private void handleRefreshToken(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) throws ServletException, IOException {
        try{

            // Extract Refresh Token
            String refreshToken = extractRefreshToken(request);
            AuthUser authUser = authUserService.loadUserByRefreshToken(UUID.fromString(refreshToken));

            // Delete used refresh token
            refreshTokenService.deleteRefreshTokenByUserId(authUser.getId());
            log.info("Remove old refresh token: {}",refreshToken);

            var authResult = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
            authResult.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );
            authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
        }catch(Exception ex){
            log.info("Refresh token validation failed", ex);
            AuthenticationException exception = toAuthenticationException(ex);
            authenticationFailureHandler.onAuthenticationFailure(request, response, exception);
        }
    }
}
