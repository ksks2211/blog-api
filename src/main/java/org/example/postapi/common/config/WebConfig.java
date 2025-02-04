package org.example.postapi.common.config;

import org.example.postapi.common.resolver.PageRequestResolver;
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
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserUUIDResolver());
        resolvers.add(new PageRequestResolver());
    }
}
