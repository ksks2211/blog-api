package org.example.postapi.security;


/**
 * @author rival
 * @since 2025-01-16
 */
public class SecurityConstants {

    public static final String LOGIN_URI_PATTERN ="/login/**";
    public static final String OAUTH2_AUTHORIZATION_BASE_URI="/oauth2/authorization";
    public static final String JWT_LOGIN_URI ="/login/jwt";
    public static final String REFRESH_LOGIN_URI ="/login/refresh";
    public static final String LOGOUT_URI ="/logout";


    public static final String AUTHENTICATION_CACHE_NAME="authentications";

    private SecurityConstants() {}
}
