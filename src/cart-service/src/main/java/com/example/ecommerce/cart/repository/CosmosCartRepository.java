package com.example.ecommerce.cart.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.example.ecommerce.cart.model.Cart;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CosmosCartRepository implements CartRepository {
    private final CosmosContainer container;

    public CosmosCartRepository(CosmosContainer cartContainer) {
        this.container = cartContainer;
    }

    @Override
    public Optional<Cart> findByUserId(String userId) {
        try {
            return Optional.of(container.readItem(userId, new PartitionKey(userId), Cart.class).getItem());
        } catch (CosmosException exception) {
            if (exception.getStatusCode() == 404) {
                return Optional.empty();
            }
            throw exception;
        }
    }

    @Override
    public Cart save(Cart cart) {
        return container.upsertItem(cart, new PartitionKey(cart.getUserId()),
                new CosmosItemRequestOptions()).getItem();
    }

    @Override
    public void deleteByUserId(String userId) {
        try {
            container.deleteItem(userId, new PartitionKey(userId), new CosmosItemRequestOptions());
        } catch (CosmosException exception) {
            if (exception.getStatusCode() != 404) {
                throw exception;
            }
        }
    }
}
