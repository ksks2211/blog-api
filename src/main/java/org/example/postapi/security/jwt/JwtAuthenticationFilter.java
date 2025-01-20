package org.example.postapi.security.jwt;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.security.AuthUser;
import org.example.postapi.security.AuthUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-16
 */

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
        .getContextHolderStrategy();
    private final JwtService jwtService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AuthUserService authUserService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        try{
            processJwtAuthentication(request);
        }catch(Exception ex){
            securityContextHolderStrategy.clearContext();
            log.debug("Failed to process jwt authentication request", ex);
            AuthenticationException exception = ex instanceof AuthenticationException ?
                (AuthenticationException) ex :
                new AuthenticationServiceException(ex.getMessage(), ex);
            this.authenticationEntryPoint.commence(request, response, exception);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void processJwtAuthentication(HttpServletRequest request)throws AuthenticationException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            return;
        }

        // Extract JWT & Verify
        String token = authHeader.substring(7);
        JwtVerifyResult jwtVerifyResult = jwtService.verifyToken(token);


        if(jwtVerifyResult.isVerified()){
            UUID userId = jwtVerifyResult.getUserId();

            // throws - AppUserNotFoundException
            AuthUser authUser = authUserService.loadUserById(userId);


            // Remove credentials
            authUser.eraseCredentials();

            // authenticated
            var authentication = new UsernamePasswordAuthenticationToken(
                authUser, null, authUser.getAuthorities());
            authentication.setDetails(
                new WebAuthenticationDetails(request)
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            log.debug("Successfully authenticated user {}", authUser);
        }else{
            log.debug("Failed to verify token");
        }


    }
}
