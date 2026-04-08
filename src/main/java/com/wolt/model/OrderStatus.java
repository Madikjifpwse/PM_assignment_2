package com.wolt.model;

import java.util.Set;
import java.util.Map;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    DELIVERED;

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
            CREATED, Set.of(CONFIRMED),
            CONFIRMED, Set.of(DELIVERED),
            DELIVERED, Set.of()
    );

    public boolean canTransitionTo(OrderStatus newStatus) {
        return VALID_TRANSITIONS.get(this).contains(newStatus);
    }

    public boolean isFinalState() {
        return VALID_TRANSITIONS.get(this).isEmpty();
    }

    public Set<OrderStatus> getValidNextStatuses() {
        return VALID_TRANSITIONS.get(this);
    }
}