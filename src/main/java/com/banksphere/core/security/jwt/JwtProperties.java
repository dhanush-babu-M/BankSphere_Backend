package com.banksphere.core.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="banksphere.jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private long expirationMs;
    private long refreshExpirationMs;
}
