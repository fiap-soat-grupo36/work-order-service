package br.com.fiap.oficina.workorder.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignAuthConfig {

    @Bean
    public RequestInterceptor internalAuthRequestInterceptor(
            @Value("${app.integration.internal-auth-token:}") String internalAuthToken
    ) {
        return template -> {
            if (internalAuthToken != null && !internalAuthToken.isBlank()) {
                template.header("Authorization", "Bearer " + internalAuthToken);
            }
        };
    }
}
