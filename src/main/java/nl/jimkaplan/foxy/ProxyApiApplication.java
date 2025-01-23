package nl.jimkaplan.foxy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
        title = "Proxy API",
        version = "1.0",
        description = "API for routing requests to multiple service providers"
    )
)
@SpringBootApplication
public class ProxyApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProxyApiApplication.class, args);
    }
}
