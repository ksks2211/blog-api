package org.example.postapi.common.config;

import lombok.RequiredArgsConstructor;
import org.example.postapi.security.resolver.CurrentUserUUIDResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author rival
 * @since 2025-01-30
 */
@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserUUIDResolver currentUserUUIDResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserUUIDResolver);
    }
}
