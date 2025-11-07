package org.example.github.config;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("scorio.github")
public record GithubProperties(
        String token,                 // may be blank (unauthenticated mode)
        @NotBlank String baseUrl,
        @NotBlank String accept,
        @NotBlank String apiVersion,
        @NotBlank String userAgent,
        @NotBlank String defaultSort,
        @NotBlank String defaultOrder,
        @Min(1) @Max(100) int defaultPerPage
) { }