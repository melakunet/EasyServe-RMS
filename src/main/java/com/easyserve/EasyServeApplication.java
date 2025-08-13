package com.easyserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.easyserve.repository")
@ComponentScan(basePackages = "com.easyserve")
@EnableScheduling
@EnableAsync
public class EasyServeApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(EasyServeApplication.class);
        app.setAdditionalProfiles("dev"); // Use "dev" by default, can be overridden
        app.run(args);

        System.out.println("\n\n✅ EasyServe Application started successfully!\n");
    }
}

