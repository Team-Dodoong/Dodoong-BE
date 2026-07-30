package com.samdasu.dodoong.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties (
        List<String> allowedOrigins
) {
}
