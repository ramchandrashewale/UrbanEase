package category.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${service.microservice.url}")
    private String serviceMicroserviceUrl;

    @Bean
    public WebClient createwebClient(WebClient.Builder builder){
        return builder.baseUrl(serviceMicroserviceUrl).build();
    }
}
