package org.example.postapi.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.common.dto.ApiResponse;
import org.example.postapi.refresh.RefreshCookieService;
import org.example.postapi.refresh.RefreshTokenService;
import org.example.postapi.security.AuthUser;
import org.example.postapi.security.AuthUserService;
import org.example.postapi.security.jwt.JwtResponse;
import org.example.postapi.security.jwt.JwtService;
import org.example.postapi.security.oauth2.CustomOidcUser;
import org.example.postapi.user.RegistrationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.Map;

import static org.example.postapi.common.util.SessionUtils.invalidateSession;

/**
 * @author rival
 * @since 2025-01-20
 */

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityHandlerConfig {

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final RefreshCookieService refreshCookieService;
    private final RefreshTokenService refreshTokenService;
    private final AuthUserService authUserService;




    @Bean
    public LogoutHandler logoutHandler(){
        return (request, response, auth) -> {
            if(auth != null && auth.getPrincipal() instanceof AuthUser authUser){
                clearRefreshToken(authUser);
                Cookie clearRefreshCookie = refreshCookieService.createClearRefreshCookie();
                response.addCookie(clearRefreshCookie);
                log.info("cookie cleared for user: {}",authUser.getId());
            }else{
                log.info("not authenticated user tried logout");
            }
        };
    }


    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(){
        return (request, response, authResult) -> {

            // 400 : bad request - 잘못된 요청으로 취급
            // 401 : unauthorized - 허가되지 않은 요청으로 취급
            // 204 : no content - 로그인 상태가 아니므로 성공한것과 마찬가지로 취급
            if(authResult != null && authResult.getPrincipal() instanceof AuthUser authUser){
                log.info("logout succeeded: {}",authUser.getId());
                var body = ApiResponse.success("logout succeeded", Map.of("message", "logout succeeded"));
                sendResponseWithBody(response, HttpServletResponse.SC_OK, body);
            }else{
                log.info("logout failed");
                var body = ApiResponse.error("logout failed");
                sendResponseWithBody(response, HttpServletResponse.SC_UNAUTHORIZED, body);
            }
        };
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return (request, response, accessDeniedException) -> {
            log.info("Access denied: {}",accessDeniedException.getMessage());
            var body = ApiResponse.error("Access denied");
            sendResponseWithBody(response, HttpServletResponse.SC_FORBIDDEN, body);
        };
    }


    // Failed authentication attempt or Access without authentication
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return (request, response, authException) -> {
            log.info("Unauthenticated: {}",authException.getMessage());
            var body = ApiResponse.error("Unauthenticated");
            sendResponseWithBody(response, HttpServletResponse.SC_UNAUTHORIZED, body);
        };
    }



    private void generateRefreshToken(HttpServletResponse response, AuthUser authUser){
        try{
            String newRefreshToken = refreshTokenService.createRefreshToken(authUser.getId());
            Cookie refreshCookie = refreshCookieService.createRefreshCookie(newRefreshToken);
            response.addCookie(refreshCookie);
            log.info("New refresh token({}) created for user: {}",newRefreshToken, authUser.getId());
        }catch(Exception e){
            log.info("Failed to generate refresh token", e);
        }
    }

    private void clearRefreshToken(AuthUser authUser){
        try{
            refreshTokenService.deleteRefreshTokenByUserId(authUser.getId());
            log.info("Refresh token cleared for user: {}",authUser.getId());
        }catch (Exception e){
            log.info("Failed to delete refresh token", e);
        }
    }


    private RegistrationProvider getRegistrationProvider(String registrationId){
        return switch(registrationId.toLowerCase()){
            case "google" -> RegistrationProvider.GOOGLE;
            case "facebook" -> RegistrationProvider.FACEBOOK;
            default -> RegistrationProvider.LOCAL;
        };
    }

    private AuthUser createOrUpdateOAuth2(RegistrationProvider provider, OidcUser oidcUser){
        try{
            String sub = oidcUser.getSubject();
            String name = oidcUser.getName();
            String email = oidcUser.getEmail();
            return authUserService.createOrUpdateOAuth2Account(provider, sub, name, email);
        }catch(Exception e){
            log.info("Failed to create or update oauth2 account", e);
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return ((request, response, authResult) -> {
            Object principal = authResult.getPrincipal();
            AuthUser authUser = null;

            if(principal instanceof AuthUser){
                authUser = (AuthUser)principal;
            }else if(principal instanceof CustomOidcUser oidcUser){
                String registrationId = oidcUser.getRegistrationId();
                log.info("RegistrationId : {}", registrationId);
                // map  registrationId => provider
                RegistrationProvider provider = getRegistrationProvider(registrationId);
                authUser = createOrUpdateOAuth2(provider, oidcUser);
            }

            if(authUser != null){
                String token = jwtService.createToken(authUser.getId());
                log.info("New jwt token for user: {}",authUser.getId());

                // Refresh Token
                generateRefreshToken(response,authUser);

                // Clear session
                invalidateSession(request.getSession(false));

                JwtResponse jwtResponse = new JwtResponse(token);
                var body = ApiResponse.success("logged in", jwtResponse);
                sendResponseWithBody(response, HttpServletResponse.SC_OK, body);
            } else{
                throw new AuthenticationServiceException("Failed to generate token for authentication");
            }

        });
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){
        return ((request, response, exception) -> {
           log.info("login attempt failed: {}",exception.getMessage());
           invalidateSession(request.getSession(false));
           var body = ApiResponse.error("login attempt failed");
           sendResponseWithBody(response, HttpServletResponse.SC_UNAUTHORIZED, body);
        });
    }



    private void sendResponseWithBody(HttpServletResponse response, int statusCode, ApiResponse<?> body) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(statusCode);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(body));
    }


}
