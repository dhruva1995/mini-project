package dc.codecademy.miniproject.configs;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(servers = {
        @Server(description = "Local Env", url = "http://localhost:8080")
}, info = @Info(license = @License(name = "License name", url = "https://license_provider.com"), version = "1.0", contact = @Contact(name = "Dhruva Chandra", email = "helloworldimanew@gmail.com"), description = "Come upload your cat images, view download whenever you want!", title = "Cat Photo store"))
@SecurityScheme(name = "bearerAuth", description = "JWT token", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenAPIConfig {

}
