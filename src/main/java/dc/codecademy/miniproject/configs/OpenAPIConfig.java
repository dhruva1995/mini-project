package dc.codecademy.miniproject.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(name = "bearerAuth", description = "JWT token", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenAPIConfig {

    @Value("${instanceUrl:'http://localhost:8080'}")
    private String instanceUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        System.out.println("Picked up url as " + instanceUrl);
        return new OpenAPI()
                .addServersItem(new Server().url(instanceUrl).description("Local Dev"))
                .info(new Info()
                        .version("1.0")
                        .contact(new Contact().email("helloworldiamnew@gmail.com").name("Dhruva Chandra"))
                        .description("Come upload your cat images, view download whenever you want!")
                        .title("Cat Photo store"));
    }
}
