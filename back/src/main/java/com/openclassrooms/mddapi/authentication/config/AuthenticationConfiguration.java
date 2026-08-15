package com.openclassrooms.mddapi.authentication.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({JwtProperties.class, AuthenticationMessagesProperties.class})
public class AuthenticationConfiguration {}
