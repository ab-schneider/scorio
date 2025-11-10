package org.example.github;

import org.example.config.CacheConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CacheConfig.class, GithubClientTestConfig.class})
class GithubClientCacheIT {

    @Autowired
    private GithubClient githubClient;

    @Autowired
    private RestClient restClient;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        Cache cache = cacheManager.getCache("githubSearch");
        if (cache != null) {
            cache.clear();
        }
        Mockito.clearInvocations(restClient);
    }

    @Test
    void shouldReuseCacheWhenLanguageDiffersByCase() {
        githubClient.searchRepositories("Java", null, "stars", "desc", 30, 1);
        githubClient.searchRepositories("java ", null, "stars", "desc", 30, 1);
        githubClient.searchRepositories(" jAVA", null, "stars", "desc", 30, 1);

        verify(restClient, times(1)).get();
    }

    @Test
    void shouldEvictCacheWhenPageSizeDiffers() {
        githubClient.searchRepositories("Java", null, "stars", "desc", 30, 1);
        githubClient.searchRepositories("Java", null, "stars", "desc", 40, 1);

        verify(restClient, times(2)).get();
    }

    @Test
    void shouldEvictCacheWhenPageNumberDiffers() {
        githubClient.searchRepositories("Java", null, "stars", "desc", 30, 1);
        githubClient.searchRepositories("Java", null, "stars", "desc", 30, 2);

        verify(restClient, times(2)).get();
    }

    @Test
    void shouldEvictCacheWhenCreatedAfterDiffers() {
        LocalDate today = LocalDate.now();
        githubClient.searchRepositories("Java", today, "stars", "desc", 30, 1);
        githubClient.searchRepositories("Java", today.plusDays(1), "stars", "desc", 30, 1);

        verify(restClient, times(2)).get();
    }
}

