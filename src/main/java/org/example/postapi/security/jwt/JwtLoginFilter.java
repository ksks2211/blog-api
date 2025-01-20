package org.example.postapi.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.user.dto.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


import java.io.IOException;
import java.io.InputStream;

import static org.example.postapi.security.SecurityConstants.JWT_LOGIN_URL;

/**
 * @author rival
 * @since 2025-01-16
 */

@Slf4j
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
    private final static String HTTP_METHOD = "POST";
    private final ObjectMapper objectMapper;
    private final Validator validator;


    public JwtLoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, Validator validator) {
        super(new AntPathRequestMatcher(JWT_LOGIN_URL, HTTP_METHOD), authenticationManager);
        this.objectMapper = objectMapper;
        this.validator= validator;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        try (InputStream inputStream = request.getInputStream()) {
            LoginRequest loginRequest = objectMapper.readValue(inputStream, LoginRequest.class);

            var violations = validator.validate(loginRequest);
            if(!violations.isEmpty()){
                log.info("Invalid username or password: {}", violations);
                throw new ConstraintViolationException(violations);
            }

            String principal = loginRequest.getEmail();
            String credentials = loginRequest.getPassword();


            // Authentication(authenticated = false)
            var token = new UsernamePasswordAuthenticationToken(principal, credentials);


            // Authentication(authenticated = true)
            return getAuthenticationManager().authenticate(token);
        }
    }
}
