package org.example.postapi.common.util;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;


/**
 * @author rival
 * @since 2025-01-16
 */
@Slf4j
public class SessionUtils {


    public static void invalidateSession(@Nullable HttpSession session){


        if(session!=null){
            String sessionId = session.getId();
            session.invalidate();
            log.info("Session(id={}) invalidated",sessionId);
        }
    }
}
