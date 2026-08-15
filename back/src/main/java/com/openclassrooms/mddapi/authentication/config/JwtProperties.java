package com.openclassrooms.mddapi.authentication.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("mdd.authentication.jwt")
public record JwtProperties(String secret, Duration expiration, boolean secureCookie) {}
