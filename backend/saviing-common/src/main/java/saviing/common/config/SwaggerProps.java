package saviing.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "springdoc")
public record SwaggerProps(List<SwaggerServer> servers) {
    public record SwaggerServer(
        String url,
        String description
    ) {}
}