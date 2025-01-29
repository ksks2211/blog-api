package org.example.postapi.security.resolver;

import lombok.extern.slf4j.Slf4j;
import org.example.postapi.security.AuthUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-30
 */
@Component
@Slf4j
public class CurrentUserUUIDResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UUID.class) && parameter.hasParameterAnnotation(CurrentUserUUID.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,@NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null && authentication.getPrincipal() instanceof AuthUser){
            return ((AuthUser)authentication.getPrincipal()).getId();
        }else{
            log.info("Cannot resolve uuid");
            throw new CurrentUserUUIDNotFoundException("Cannot resolve uuid");
        }
    }
}
