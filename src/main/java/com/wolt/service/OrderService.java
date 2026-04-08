package com.wolt.service;

import com.wolt.cache.OrderCache;
import com.wolt.model.Order;
import com.wolt.model.OrderStatus;
import com.wolt.repository.FileOrderRepository;
import com.wolt.repository.OrderRepository;
import com.wolt.util.Logger;

import java.util.List;
import java.util.Optional;

public class OrderService {
    private final OrderRepository repository;
    private final OrderCache cache;
    private final Logger logger = Logger.getInstance();

    public OrderService() {
        this.repository = new FileOrderRepository();
        this.cache = new OrderCache();

        if (repository instanceof FileOrderRepository) {
            cache.loadAll(((FileOrderRepository) repository).getAllAsMap());
        }

        logger.info("OrderService initialized");
    }

    public Order createOrder(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Order description cannot be empty");
        }

        Order order = Order.create(description.trim());
        repository.save(order);
        cache.put(order);

        logger.info("Created new order: " + order.getId() + " - " + description);
        return order;
    }

    public Optional<Order> findOrderById(String orderId) {
        Optional<Order> cachedOrder = cache.get(orderId);

        if (cachedOrder.isPresent()) {
            return cachedOrder;
        }

        Optional<Order> order = repository.findById(orderId);

        order.ifPresent(cache::put);

        return order;
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public List<Order> findOrdersByStatus(OrderStatus status) {
        return repository.findByStatus(status);
    }

    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = findOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus oldStatus = order.getStatus();

        try {
            order.updateStatus(newStatus);
            repository.save(order);
            cache.put(order);

            logger.info(String.format("Updated order %s status: %s -> %s",
                    orderId, oldStatus, newStatus));
            return order;

        } catch (IllegalStateException | IllegalArgumentException e) {
            logger.error("Failed to update order status: " + e.getMessage());
            throw e;
        }
    }

    public Order updateOrderDescription(String orderId, String newDescription) {
        if (newDescription == null || newDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        Order order = findOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        try {
            order.updateDescription(newDescription.trim());
            repository.save(order);
            cache.put(order);

            logger.info("Updated order " + orderId + " description");
            return order;

        } catch (IllegalStateException e) {
            logger.error("Failed to update order description: " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteOrder(String orderId) {
        boolean deleted = repository.delete(orderId);
        if (deleted) {
            cache.remove(orderId);
            logger.info("Deleted order: " + orderId);
        }
        return deleted;
    }

    public String getCacheStats() {
        return cache.getStats();
    }

    public int getOrderCount() {
        return repository.count();
    }
    public void clearCache() {
        cache.clear();
        logger.info("OrderService: Cache cleared.");
    }
}