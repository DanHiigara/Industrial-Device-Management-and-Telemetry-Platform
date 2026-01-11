package com.ndiii.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtGatewaySecurityConfig {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(ex -> ex
            .pathMatchers("/api/auth/**").permitAll()
            .pathMatchers("/api/telemetry/**").permitAll() // device API key protects telemetry
            .anyExchange().authenticated()
        )
        .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> {}))
        .build();
  }

  @Bean
  public ReactiveJwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secret) {
    var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    return NimbusReactiveJwtDecoder.withSecretKey(key).build();
  }
}
