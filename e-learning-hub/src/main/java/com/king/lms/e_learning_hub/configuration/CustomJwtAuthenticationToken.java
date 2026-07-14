package com.king.lms.e_learning_hub.configuration;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class CustomJwtAuthenticationToken extends JwtAuthenticationToken{

    private final Long userId;

    public CustomJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, Long userId) {
        super(jwt, authorities, jwt.getSubject());
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    
    
}
