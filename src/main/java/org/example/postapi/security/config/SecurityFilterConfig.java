package org.example.postapi.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.example.postapi.domain.refresh.RefreshCookieService;
import org.example.postapi.domain.refresh.RefreshTokenService;
import org.example.postapi.security.AuthUserService;
import org.example.postapi.security.jwt.JwtAuthenticationFilter;
import org.example.postapi.security.jwt.JwtLoginFilter;
import org.example.postapi.security.jwt.JwtRefreshFilter;
import org.example.postapi.security.jwt.JwtService;
import org.example.postapi.security.oauth2.NoOpOAuth2AuthorizedClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

import static org.example.postapi.security.SecurityConstants.*;
import static org.example.postapi.domain.user.AppUserConstants.ACCOUNT_URL_PATTERN;

/**
 * @author rival
 * @since 2025-01-16
 */

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityFilterConfig {


    @Value("${server.servlet.context-path}")
    private String SERVLET_CONTEXT_PATH;

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final AuthenticationConfiguration authConfig;

    // Services
    private final JwtService jwtService;
    private final AuthUserService authUserService;
    private final RefreshCookieService refreshCookieService;
    private final RefreshTokenService refreshTokenService;


    // Handlers
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final LogoutHandler logoutHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;





    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfig.getAuthenticationManager();
    }


    // Filters


    private JwtRefreshFilter jwtRefreshFilter(){
        return new JwtRefreshFilter(SERVLET_CONTEXT_PATH + REFRESH_LOGIN_URI,
            refreshCookieService,refreshTokenService, authUserService,
            authenticationSuccessHandler,authenticationFailureHandler);
    }

    private JwtLoginFilter jwtLoginFilter() throws Exception {
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager(), objectMapper, validator);
        jwtLoginFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        jwtLoginFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return jwtLoginFilter;
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(jwtService,authenticationEntryPoint,authUserService);
    }




    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final var config = new CorsConfiguration();

        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "DELETE", "PUT"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setExposedHeaders(Collections.singletonList("*"));

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CORS
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()));


        // CSRF : disable
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(config -> config
                .requestMatchers(
                    new AntPathRequestMatcher(LOGIN_URI_PATTERN),
                    new AntPathRequestMatcher(ACCOUNT_URL_PATTERN),
                    new AntPathRequestMatcher("/error")
                    ).permitAll()
//                .requestMatchers(new AntPathRequestMatcher(LOGOUT_URI)).authenticated()
                .anyRequest().authenticated()
            );


        // Session Policy = Stateless
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::none)
        );


        // Insert JWT filters
        http.addFilterBefore(jwtRefreshFilter(), BasicAuthenticationFilter.class);
        http.addFilterAt(jwtAuthenticationFilter(), BasicAuthenticationFilter.class);
        http.addFilterAt(jwtLoginFilter(), UsernamePasswordAuthenticationFilter.class);



        http.exceptionHandling(config->config
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        );



        http.logout(config -> config
            .addLogoutHandler(logoutHandler)
            .invalidateHttpSession(true)
            .logoutUrl(LOGOUT_URI)
            .logoutSuccessHandler(logoutSuccessHandler)
        );



        // oauth2
        http.oauth2Login(config -> config
            .authorizationEndpoint(endpoint -> endpoint.baseUri(OAUTH2_AUTHORIZATION_BASE_URI))
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
            .authorizedClientService(new NoOpOAuth2AuthorizedClientService())
        );


        var chain = http.build();
        reorderFilters(chain.getFilters());


        return chain;
    }



    // Move LogoutFilter behind JwtAuthenticationFilter
    private void reorderFilters(List<Filter> filters){
        LogoutFilter logoutFilter = null;

        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i) instanceof LogoutFilter) {
                logoutFilter = (LogoutFilter) filters.remove(i);


                break;
            }
        }

        if (logoutFilter != null) {
            for (int i = 0; i < filters.size(); i++) {
                if (filters.get(i) instanceof JwtAuthenticationFilter) {
                    filters.add(i + 1, logoutFilter);
                    break;
                }
            }
        }
    }
}
