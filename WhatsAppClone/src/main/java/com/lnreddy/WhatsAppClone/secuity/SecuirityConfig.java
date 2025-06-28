package com.lnreddy.WhatsAppClone.secuity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecuirityConfig {

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req-> req
                                        .requestMatchers("/auth/**",
                                                "/v2/api-docs",
                                                "/v3/api-docs",
                                                "/v3/api-docs/**",
                                                "/swagger-resources",
                                                "/swagger-resources/**",
                                                "/configuration/ui",
                                                "/configuration/security",
                                                "/swagger-ui/**",
                                                "/webjars/**",
                                                "/swagger-ui.html",
                                                "/ws/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated()
                ).oauth2ResourceServer( auth -> auth
                        .jwt(token -> token.jwtAuthenticationConverter(new KeyCloakJwtAuthenticationConverter()))
                );
        return httpSecurity.build();
    }

    @Bean
    public CorsFilter corsFilter() {
  CorsConfiguration config = new CorsConfiguration();
  config.setAllowCredentials(true);
       config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));            // Allow requests from any domain
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.ORIGIN
        ));
       config.setAllowedMethods(Stream.of(
               HttpMethod.POST,
               HttpMethod.GET,
               HttpMethod.OPTIONS,
               HttpMethod.HEAD,
               HttpMethod.PATCH,
               HttpMethod.DELETE,
               HttpMethod.PUT
       ).map(HttpMethod::name).toList());
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

