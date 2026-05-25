package com.alexandre.doeflow.infra.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth publico
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                        // Visitante
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/campaign").permitAll()
                        .requestMatchers(HttpMethod.GET, "/campaign/**").permitAll()

                        // Campanhas
                        .requestMatchers(HttpMethod.PATCH, "/campaign/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/campaign/*/refuse").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/campaign").hasAnyRole("CREATOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/campaign/**").hasAnyRole("CREATOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/campaign/**").hasAnyRole("CREATOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/campaign/**").hasRole("ADMIN")

                        // Doacoes
                        .requestMatchers(HttpMethod.POST, "/donation").hasAnyRole("DONOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/donation/my").hasAnyRole("DONOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/donation").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/donation/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/donation/**").hasRole("ADMIN")

                        // Usuario logado
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()

                        // Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder(@Value("${api.security.token.secret}") String secret) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(buildSecretKey(secret)));
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${api.security.token.secret}") String secret) {
        return NimbusJwtDecoder.withSecretKey(buildSecretKey(secret))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    private SecretKey buildSecretKey(String secret) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, HMAC_ALGORITHM);
    }
}
