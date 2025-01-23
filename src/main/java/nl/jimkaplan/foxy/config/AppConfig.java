package nl.jimkaplan.foxy.config;

import nl.jimkaplan.foxy.model.Provider;
import nl.jimkaplan.foxy.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${app.openai.api-key}")
    private String openaiApiKey;

    @Bean
    public CommandLineRunner init(ProviderRepository repository) {
        return args -> {
            if (repository.findByName("openai") == null) {
                Provider defaultProvider = new Provider();
                defaultProvider.setName("openai");
                defaultProvider.setUrl("https://api.openai.com/v1/");
                defaultProvider.setApiKey(openaiApiKey);
                defaultProvider.setUsageFlag(true);
                defaultProvider.setPriority(1);
                repository.save(defaultProvider);
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}