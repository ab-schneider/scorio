package org.example.github;

import org.example.github.dto.GithubSearchResponse;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@TestConfiguration
class GithubClientTestConfig {

    @Bean
    RestClient restClient() {
        RestClient restClient = Mockito.mock(RestClient.class);
        RestClient.RequestHeadersUriSpec<?> requestSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);

        Mockito.when(restClient.get()).thenAnswer(invocation -> requestSpec);
        Mockito.when(requestSpec.uri(anyString())).thenAnswer(invocation -> requestSpec);
        Mockito.when(requestSpec.accept(Mockito.any())).thenAnswer(invocation -> requestSpec);
        Mockito.when(requestSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(eq(GithubSearchResponse.class)))
                .thenAnswer(invocation -> sampleResponse());

        return restClient;
    }

    @Bean
    GithubClient githubClient(RestClient restClient) {
        return new GithubClient(restClient);
    }

    private GithubSearchResponse sampleResponse() {
        GithubSearchResponse.Item item = new GithubSearchResponse.Item(
                "owner/repo",
                "https://example.com/repo",
                "java",
                100,
                10,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                "description"
        );
        return new GithubSearchResponse(1, false, List.of(item));
    }
}

