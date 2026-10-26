package com.example.ecommerce.reporting;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ReportingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportingServiceApplication.class, args);
    }

    @Bean
    CosmosClient cosmosClient(@Value("${azure.cosmos.endpoint}") String endpoint) {
        return new CosmosClientBuilder()
                .endpoint(endpoint)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    @Bean
    CosmosContainer reportingContainer(
            CosmosClient client,
            @Value("${azure.cosmos.database:ecommerce}") String database,
            @Value("${azure.cosmos.container:reports}") String container) {
        return client.getDatabase(database).getContainer(container);
    }
}
