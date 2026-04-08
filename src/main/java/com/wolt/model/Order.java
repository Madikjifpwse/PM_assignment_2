package com.wolt.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private final String id;
    private String description;
    private OrderStatus status;

    private final Map<OrderStatus, LocalDateTime> stateTimestamps;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Order(String id, String description, OrderStatus status,
                  Map<OrderStatus, LocalDateTime> stateTimestamps,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.stateTimestamps = stateTimestamps != null ? new HashMap<>(stateTimestamps) : new HashMap<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order create(String description) {
        LocalDateTime now = LocalDateTime.now();
        Map<OrderStatus, LocalDateTime> timestamps = new HashMap<>();
        timestamps.put(OrderStatus.CREATED, now);

        return new Order(
                UUID.randomUUID().toString(),
                description,
                OrderStatus.CREATED,
                timestamps,
                now,
                now
        );
    }

    public static Order reconstruct(String id, String description, OrderStatus status,
                                    Map<OrderStatus, LocalDateTime> stateTimestamps,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Order(id, description, status, stateTimestamps, createdAt, updatedAt);
    }

    public void updateStatus(OrderStatus newStatus) {
        if (status.isFinalState()) {
            throw new IllegalStateException(
                    String.format("Cannot modify order %s - already in final state: %s", id, status)
            );
        }

        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalArgumentException(
                    String.format("Invalid transition from %s to %s for order %s",
                            status, newStatus, id)
            );
        }

        this.status = newStatus;
        this.stateTimestamps.put(newStatus, LocalDateTime.now());
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDescription(String description) {
        if (status.isFinalState()) {
            throw new IllegalStateException(
                    String.format("Cannot modify order %s - already in final state: %s", id, status)
            );
        }
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getStatusTimestamp(OrderStatus status) {
        return stateTimestamps.get(status);
    }

    public Map<OrderStatus, LocalDateTime> getStateTimestamps() {
        return new HashMap<>(stateTimestamps);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Order{id='%s', description='%s', status=%s, createdAt=%s}",
                id, description, status, createdAt);
    }
}