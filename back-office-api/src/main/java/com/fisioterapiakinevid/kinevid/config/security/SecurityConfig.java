package com.fisioterapiakinevid.kinevid.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 * Configuración de seguridad Spring Security
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // En desarrollo: http://localhost:4200 | En producción: dominio real del frontend
    @Value("${kinevid.app.cors.allowed-origins:http://localhost:4200}")
    private String corsAllowedOrigins;

    /**
     * Bean principal de la cadena de filtros de seguridad
     * Define:
     * 1. Rutas públicas (sin autenticación)
     * 2. Rutas protegidas (requieren autenticación)
     * 3. Orden correcto de filtros JWT
     * 4. Políticas de sesión STATELESS (sin cookies)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desabilitar CSRF (usamos JWT, no cookies de sesión)
                .csrf(csrf -> csrf.disable())

                //CORS centralizado
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Política de sesión: STATELESS
                // Cada request es independiente, no se mantiene estado en servidor
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Definir autorización por rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas - NO requieren autenticación
                        .requestMatchers(
                                "/api/auth/login",              //  Login (acceso público)
                                "/api/auth/refresh",            //  el access token puede estar expirado
                                "/api/auth/forgot-password",    //  Solicitar recuperación contraseña
                                "/api/auth/reset-password",     //  Validar código y cambiar contraseña
                                "/swagger-ui/**",               //  Swagger UI (documentación)
                                "/v3/api-docs/**",              //  OpenAPI docs
                                "/actuator/health"              //  Health check
                        ).permitAll()

                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated()
                )

                // Punto de entrada para errores de autenticación (retorna JSON en lugar de HTML)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // Provider de autenticación (DAO - base de datos)
                .authenticationProvider(authenticationProvider())
                // Orden explícito: ExceptionHandlerFilter → JwtAuthFilter → UsernamePasswordAuthFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false); // Solo debe ejecutarse dentro de Spring Security
        return registration;
    }

    @Bean
    public FilterRegistrationBean<ExceptionHandlerFilter> exceptionHandlerFilterRegistration(
            ExceptionHandlerFilter filter) {
        FilterRegistrationBean<ExceptionHandlerFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    // ───  Configuración CORS centralizada ─────────────────────────────

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList(corsAllowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}