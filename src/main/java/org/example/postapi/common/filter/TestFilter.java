package org.example.postapi.common.filter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author rival
 * @since 2025-01-14
 */
@Component
@Slf4j
public class TestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,@Nonnull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Once per Req Filter");
        filterChain.doFilter(request, response);
    }
}
