package com.wolt.cache;

import com.wolt.model.Order;
import com.wolt.util.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OrderCache {
    private final Map<String, Order> cache = new HashMap<>();
    private final Logger logger = Logger.getInstance();

    private int hits = 0;
    private int misses = 0;

    public Optional<Order> get(String orderId) {
        Order order = cache.get(orderId);

        if (order != null) {
            hits++;
            logger.info("Cache HIT for order: " + orderId);
            return Optional.of(order);
        } else {
            misses++;
            logger.info("Cache MISS for order: " + orderId);
            return Optional.empty();
        }
    }

    public void put(Order order) {
        cache.put(order.getId(), order);
        logger.info("Cached order: " + order.getId());
    }

    public void remove(String orderId) {
        cache.remove(orderId);
        logger.info("Removed from cache: " + orderId);
    }

    public void loadAll(Map<String, Order> orders) {
        cache.putAll(orders);
        logger.info("Loaded " + orders.size() + " orders into cache");
    }

    public void clear() {
        int size = cache.size();
        cache.clear();
        logger.info("Cache cleared. Removed " + size + " orders");
    }

    public int size() {
        return cache.size();
    }

    public String getStats() {
        int total = hits + misses;
        double hitRate = total > 0 ? (double) hits / total * 100 : 0;

        return String.format("Cache Stats - Size: %d | Hits: %d | Misses: %d | Hit Rate: %.2f%%",
                size(), hits, misses, hitRate);
    }

    public void resetStats() {
        hits = 0;
        misses = 0;
        logger.info("Cache statistics reset");
    }

}