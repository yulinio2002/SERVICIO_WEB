package com.example.proyecto.config;

import com.example.proyecto.exception.RestAccessDeniedHandler;
import com.example.proyecto.exception.RestAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint authEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Aquí registras tus nuevos handlers
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas de autenticación
                        .requestMatchers( "/auth/login").permitAll()
                        // Ruta /auth/me requiere autenticación
                        .requestMatchers("/auth/me").authenticated()
                        // Documentación API
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // Rutas específicas por rol
                        // Empresas
                        .requestMatchers(HttpMethod.GET, "/api/empresas/", "/api/empresas/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/empresas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/empresas/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/empresas/{id}").hasRole("ADMIN")

                        // Fotos
                        .requestMatchers(HttpMethod.GET, "/api/fotos/", "/api/fotos/servicios/{servicioId}","/api/fotos/proyectos/{proyectoId}","/api/fotos/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/fotos/servicios/{servicioId}","/api/fotos/proyectos/{proyectoId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/fotos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/fotos/{id}").hasRole("ADMIN")

                        // Marcas
                        .requestMatchers(HttpMethod.GET, "/api/marcas/", "/api/marcas/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/marcas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/marcas/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/marcas/{id}").hasRole("ADMIN")

                        // Servicios
                        .requestMatchers(HttpMethod.GET, "/api/servicios", "/api/servicios/", "/api/servicios/{id}", "/api/servicios/top/5").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/servicios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/servicios/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/servicios/{id}").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        // Productos
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/productos", "/api/productos/",
                                "/api/productos/{id}",
                                "/api/productos/top/5"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/{id}").hasRole("ADMIN")
                        // Proyectos
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/proyectos", "/api/proyectos/",
                                "/api/proyectos/{id}",
                                "/api/proyectos/top/5"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/proyectos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/proyectos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/proyectos/{id}").hasRole("ADMIN")

                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll()
                )
                .authenticationProvider(daoAuthProvider())
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:5173"));
//        cfg.setAllowedOrigins(List.of("*"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
