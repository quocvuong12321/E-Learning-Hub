package com.king.lms.e_learning_hub.configuration;


import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.signerKey}")
    private String signKey;

    private static final String[] PUBLIC_ENDPOINTS =
            {"/users",
                    "/auth/login",
                    "/auth/refresh",
                    "/account/register",
//            "/auth/callback/**",        // OAuth2 callback
                    "/auth/oauth2/**",          // ✅ OAuth2 endpoints
//            "/auth/google/callback",    // ✅ Google callback
            };

    private static final String[] PUBLIC_GET_ENDPOINTS =
            {"/post/**",
                    "/swagger-ui.html",
                    "/v3/api-docs",
                    "/category/**",
                    "/image/**",
                    "/course-category/**",
                    "/auth/oauth2/**"
            };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        //1. Cấu hình quyền truy cập
        httpSecurity
                .cors(cors -> cors.configurationSource(configurationSource()))
                // Kích hoạt CORS, tự lấy CorsConfigurationSource bean
                .csrf(AbstractHttpConfigurer::disable)
                // Phân quyền
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                                .anyRequest().authenticated()
                );
        //2. Cấu hình resource server để giải mã JWT
        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );
        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signKey.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }


    // Cấu hình CORS
    CorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
        corsConfiguration.setAllowedMethods(List.of("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        //QUAN TRỌNG: Cho phép Frontend đọc được Header Authorization (chứa JWT)
        corsConfiguration.setAllowCredentials(true); // Phải là false khi dùng "*"
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


}
