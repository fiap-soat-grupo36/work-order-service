package br.com.fiap.oficina.workorder.config;

import br.com.fiap.oficina.shared.security.JwtAuthenticationFilter;
import br.com.fiap.oficina.shared.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
// @EnableMethodSecurity  // ← COMENTADO TEMPORARIAMENTE
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String ATENDENTE = "ATENDENTE";
    private static final String MECANICO = "MECANICO";
    private static final String CLIENTE = "CLIENTE";

    private static final String[] PUBLIC_GET = {
            "/actuator/health",
            "/swagger-ui/**",
        "/v3/api-docs/**",
        "/api/ordens-servico",      // ← TEMPORÁRIO
        "/api/ordens-servico/**"    // ← TEMPORÁRIO
    };

    private final JwtTokenProvider tokenProvider;

    @Autowired
    public SecurityConfig(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(tokenProvider);

return http
    .csrf(AbstractHttpConfigurer::disable)
    .headers(AbstractHttpConfigurer::disable)
    .authorizeHttpRequests(auth -> auth
        .anyRequest().permitAll()
    )
    .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
    .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
