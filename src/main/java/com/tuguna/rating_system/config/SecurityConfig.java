package com.tuguna.rating_system.config;

import com.tuguna.rating_system.service.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1) Disable CSRF for API
                .csrf(csrf -> csrf.disable())

                // 2) Disable CORS
                .cors(cors -> cors.disable())

                // 3)Spring Security 6 blocks POST requests unless this is disabled
                .requestCache(cache -> cache.disable())

                // 4) Disable form login / logout to avoid interfering with POST requests
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthEntryPoint())   // 401
                        .accessDeniedHandler(customAccessDeniedHandler())   // 403
                )

                // 5) Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // -------------------------
                        // AUTH + PUBLIC ENDPOINTS
                        // -------------------------
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/sellers/**").permitAll()
                        .requestMatchers("/games/**").permitAll()
                        // PUBLIC Users endpoints
                        .requestMatchers(HttpMethod.GET, "/users", "/users/*").permitAll()
                        // PUBLIC comment endpoints
                        .requestMatchers(HttpMethod.GET, "/users/*/comments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/*/comments/**").permitAll()

                        // Seller-only endpoint for written comments
                        .requestMatchers(HttpMethod.GET, "/users/*/comments/my").authenticated()

                        // Author-only delete/update (checked by @PreAuthorize)
                        .requestMatchers(HttpMethod.PUT, "/users/*/comments/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/*/comments/*").authenticated()

                        // GAME OBJECT ENDPOINTS

                        // Public
                        .requestMatchers(HttpMethod.GET, "/object", "/object/*").permitAll()

                        // Seller-only
                        .requestMatchers(HttpMethod.GET, "/object/my").hasRole("SELLER")
                        .requestMatchers(HttpMethod.POST, "/object").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PUT, "/object/*").hasRole("SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/object/*").hasRole("SELLER")

                        // Admin routes
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Everything else → must be logged in
                        .anyRequest().authenticated()
                )

                // 6) Add JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // Required for authentication (used by AuthService)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Password hashing for user registration + login
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint customAuthEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("""
        {
          "status": 401,
          "errorCode": "UNAUTHORIZED",
          "message": "You must be logged in to access this resource"
        }
        """);
        };
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write("""
        {
          "status": 403,
          "errorCode": "ACCESS_DENIED",
          "message": "You do not have permission to perform this action"
        }
        """);
        };
    }
}