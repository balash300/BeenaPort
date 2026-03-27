package az.beenaport.authservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // server list
                .addServersItem(new Server()
                        //nginx prefix
                        .url("/api/invoice")
                        .description("API Gateway"));
    }
}
