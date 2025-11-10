package org.example.github.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GithubClientConfig {

    @Bean
    RestClient githubRestClient(GithubProperties properties) {
        var builder = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("Accept", properties.accept())
                .defaultHeader("X-GitHub-Api-Version", properties.apiVersion())
                .defaultHeader("User-Agent", properties.userAgent());

        if (properties.token() != null && !properties.token().isBlank()) {
            builder.defaultHeader("Authorization", "Bearer " + properties.token());
        }

        return builder.build();
    }
}
