package dc.codecademy.miniproject;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import dc.codecademy.miniproject.repositories.UserRepository;
import dc.codecademy.miniproject.services.JPAUserDetailsService;
import lombok.extern.log4j.Log4j2;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableMethodSecurity
@EnableSwagger2
@Log4j2
public class MiniProjectApplication {

	@Value("${storage.path}")
	private String storagePath;

	@Autowired
	private ApplicationContext context;

	public static void main(String[] args) {
		SpringApplication.run(MiniProjectApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(UserRepository usersRepo, JPAUserDetailsService userDetailsService) {

		return args -> {
			if (storagePath == null || storagePath.isEmpty()) {
				log.fatal("=========================  SHUTTING DOWN  ========================");
				log.fatal("Environment variable storage.path is missing or empty!");
				log.fatal("==================================================================");
				SpringApplication.exit(context, () -> -1);
				return;
			}
			log.info("Picked storage.path as {}", storagePath);
			if (!Files.exists(Paths.get(storagePath))) {
				log.fatal("=========================  SHUTTING DOWN  ========================");
				log.fatal("Value passed to Environment variable storage.path doesn't exists!");
				log.fatal("==================================================================");
				SpringApplication.exit(context, () -> -2);
				return;
			}
			userDetailsService.saveUser("user", "password");
			userDetailsService.saveUser("user2", "password");
		};
	}

}
