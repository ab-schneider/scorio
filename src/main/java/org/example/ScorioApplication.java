package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "org.example")
public class ScorioApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScorioApplication.class, args);
    }
}