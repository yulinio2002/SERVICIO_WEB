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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity(prePostEnabled = true)
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
                        // Auth pública
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Endpoints públicos (GET)
                        .requestMatchers(HttpMethod.GET,
                                "/api/empresas", "/api/empresas/*",
                                "/api/marcas", "/api/marcas/*",
                                "/api/servicios", "/api/servicios/*", "/api/servicios/top/5",
                                "/api/productos", "/api/productos/*", "/api/productos/top/5",
                                "/api/proyectos", "/api/proyectos/*", "/api/proyectos/top/5",
                                "/api/fotos", "/api/fotos/*",
                                "/api/fotos/servicios/*",
                                "/api/fotos/proyectos/*"
                        ).permitAll()

                        // ADMIN (POST/PUT/DELETE)
                        .requestMatchers(HttpMethod.POST,
                                "/api/empresas", "/api/marcas", "/api/servicios", "/api/productos", "/api/proyectos"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/empresas/*", "/api/marcas/*", "/api/servicios/*", "/api/productos/*", "/api/proyectos/*", "/api/fotos/*"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/empresas/*", "/api/marcas/*", "/api/servicios/*", "/api/productos/*", "/api/proyectos/*", "/api/fotos/*"
                        ).hasRole("ADMIN")

                        // Crear fotos (ADMIN)
                        .requestMatchers(HttpMethod.POST,
                                "/api/fotos/servicios/*", "/api/fotos/proyectos/*"
                        ).hasRole("ADMIN")
                        //registro
                        .requestMatchers(HttpMethod.POST, "/auth/register").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/auth/*").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/auth").permitAll()

                        // Enviar correo
                        .requestMatchers(HttpMethod.POST, "/api/contacto"). permitAll()

                        // /auth/me requiere auth
                        .requestMatchers("/auth/me").authenticated()


                        // TODO lo demás bajo /api requiere auth
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
