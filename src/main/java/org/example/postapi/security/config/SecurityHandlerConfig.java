package org.example.postapi.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.common.dto.ApiResponse;
import org.example.postapi.security.AuthUser;
import org.example.postapi.security.jwt.JwtResponse;
import org.example.postapi.security.jwt.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

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



    @Bean
    public LogoutHandler logoutHandler(){
        return (request, response, auth) -> {
            if(auth != null && auth.getPrincipal() instanceof AuthUser authUser){
                log.info("handle logout: {}",authUser.getId());
            }
        };
    }


    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(){
        return (request, response, auth) -> {

            // 400 : bad request - 잘못된 요청으로 취급
            // 401 : unauthorized - 허가되지 않은 요청으로 취급
            // 204 : no content - 로그인 상태가 아니므로 성공한것과 마찬가지로 취급
            if(auth != null && auth.getPrincipal() instanceof AuthUser authUser){
                log.info("logout succeeded: {}",authUser.getId());
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else{
                log.info("logout failed");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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




    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return ((request, response, authResult) -> {
            Object principal = authResult.getPrincipal();
            AuthUser authUser = null;

            if(principal instanceof AuthUser){
                authUser = (AuthUser)principal;
            }

            if(authUser != null){
                String token = jwtService.createToken(authUser.getId());
                log.info("New jwt token for user: {}",authUser.getId());

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



    private void sendResponseWithBody(HttpServletResponse response, int statusCode, Object body) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(statusCode);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(body));
    }


}
