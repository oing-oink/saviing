package saviing.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@EnableConfigurationProperties(SwaggerProps.class)
public class SwaggerConfig {

    @Value("${api.version}")
    private String apiVersion;

    @Value("${springdoc.swagger-ui.title}")
    private String apiTitle;

    @Value("${springdoc.swagger-ui.description}")
    private String apiDescription;

    @Bean
    public OpenAPI openAPI(SwaggerProps props) {
        var info = new Info()
            .title(apiTitle)
            .description(apiDescription)
            .version(apiVersion);

        List<Server> servers = props.servers() == null ? List.of()
            : props.servers().stream()
                .map(s -> new Server().url(s.url()).description(s.description()))
                .toList();

        SecurityScheme bearerAuth = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList("bearerAuth");

        return new OpenAPI()
            .info(info)
            .servers(servers)
            .components(new Components().addSecuritySchemes("bearerAuth", bearerAuth))
            .addSecurityItem(securityRequirement);
    }
}