package com.example.ecommerce.cart.config;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosmosConfiguration {
    @Bean
    CosmosClient cosmosClient(@Value("${azure.cosmos.endpoint}") String endpoint) {
        return new CosmosClientBuilder()
                .endpoint(endpoint)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    @Bean
    CosmosContainer cartContainer(
            CosmosClient client,
            @Value("${azure.cosmos.database:ecommerce}") String database,
            @Value("${azure.cosmos.container:carts}") String container) {
        return client.getDatabase(database).getContainer(container);
    }
}
