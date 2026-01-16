package com.infy.icinema.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Autowired
        private JwtAuthFilter authFilter;

        @Autowired
        private AuthenticationProvider authenticationProvider;

        // Inject the source you defined in WebConfig
        @Autowired
        private CorsConfigurationSource corsConfigurationSource;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                // Explicitly pass the configuration source
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .authorizeHttpRequests(auth -> auth
                                                // Public Endpoints
                                                .requestMatchers("/api/users/register/**", "/api/users/login/**",
                                                                "/actuator/health/**", "/booking/preview/**",
                                                                "/ws/**")
                                                .permitAll()
                                                // Public GET endpoints (viewing data)
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/movies/**",
                                                                "/api/shows/**", "/api/theatres/**", "/api/screens/**")
                                                .permitAll()
                                                // Secured Admin Endpoints (Modification)
                                                .requestMatchers("/api/movies/**", "/api/shows/**", "/api/theatres/**",
                                                                "/api/screens/**")
                                                .hasAuthority("ROLE_ADMIN")
                                                .anyRequest().authenticated())
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }
}
