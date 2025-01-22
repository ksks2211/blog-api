package org.example.postapi.refresh;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

import static org.example.postapi.security.SecurityConstants.REFRESH_LOGIN_URI;

/**
 * @author rival
 * @since 2025-01-16
 */
@Service
public class RefreshCookieService {
    @Value("${auth.refresh-token.exp-hours}")
    private int REFRESH_EXP_HOURS;

    @Value("${auth.refresh-token.name}")
    private String REFRESH_COOKIE_NAME;

    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;


    public String getRefreshCookieName(){
        return this.REFRESH_COOKIE_NAME;
    }


    public Cookie createRefreshCookie(String refreshToken){
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME,refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath(CONTEXT_PATH+ REFRESH_LOGIN_URI);
        cookie.setMaxAge(60*60*REFRESH_EXP_HOURS);
        return cookie;
    }

    public Cookie createClearRefreshCookie(){
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME,"");
        cookie.setHttpOnly(true);
        cookie.setPath(CONTEXT_PATH+ REFRESH_LOGIN_URI);
        cookie.setMaxAge(0);
        return cookie;
    }


    public Optional<Cookie> extractRefreshCookie(Cookie[] cookies){
        if(cookies == null || cookies.length==0){
            return Optional.empty();
        }
        return Arrays.stream(cookies).filter(cookie->cookie.getName().equals(REFRESH_COOKIE_NAME)).findFirst();
    }


}
