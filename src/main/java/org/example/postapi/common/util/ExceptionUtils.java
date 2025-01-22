package org.example.postapi.common.util;


import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

/**
 * @author rival
 * @since 2025-01-23
 */
public class ExceptionUtils {

    public static AuthenticationException toAuthenticationException(Exception ex) {
        return ex instanceof AuthenticationException ? (AuthenticationException)ex : new AuthenticationServiceException(ex.getMessage(), ex);
    }

    private ExceptionUtils(){}
}
