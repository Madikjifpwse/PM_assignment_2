package com.wolt.repository;

import com.google.gson.reflect.TypeToken;
import com.wolt.model.Order;
import com.wolt.model.OrderStatus;
import com.wolt.util.JsonUtil;
import com.wolt.util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileOrderRepository implements OrderRepository {
    private static final String DATA_FILE = "data/orders.json";
    private final Logger logger = Logger.getInstance();

    private final Map<String, Order> orders;

    public FileOrderRepository() {
        this.orders = new HashMap<>();
        initializeDataDirectory();
        loadOrdersFromFile();
        logger.info("FileOrderRepository initialized with " + orders.size() + " orders");
    }

    private void initializeDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_FILE).getParent();
            if (dataPath != null && !Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                logger.info("Created data directory: " + dataPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create data directory", e);
            throw new RuntimeException("Cannot initialize repository", e);
        }
    }

    private void loadOrdersFromFile() {
        Path filePath = Paths.get(DATA_FILE);

        if (!Files.exists(filePath)) {
            logger.info("Data file not found. Creating new file: " + DATA_FILE);
            saveOrdersToFile();
            return;
        }

        try {
            String json = Files.readString(filePath);

            if (json.trim().isEmpty()) {
                logger.info("Data file is empty. Starting with empty repository");
                return;
            }

            TypeToken<Map<String, OrderData>> typeToken = new TypeToken<>() {};
            Map<String, OrderData> orderDataMap = JsonUtil.fromJson(json, typeToken);

            if (orderDataMap != null) {
                orderDataMap.forEach((id, data) -> {
                    Order order = Order.reconstruct(
                            data.id,
                            data.description,
                            data.status,
                            data.stateTimestamps,
                            data.createdAt,
                            data.updatedAt
                    );
                    orders.put(id, order);
                });
            }

            logger.info("Loaded " + orders.size() + " orders from file");

        } catch (IOException e) {
            logger.error("Failed to load orders from file", e);
            throw new RuntimeException("Cannot load orders", e);
        }
    }

    private void saveOrdersToFile() {
        try {
            // Convert Order objects to OrderData for serialization
            Map<String, OrderData> orderDataMap = orders.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> OrderData.fromOrder(entry.getValue())
                    ));

            String json = JsonUtil.toJson(orderDataMap);
            Files.writeString(Paths.get(DATA_FILE), json);

            logger.info("Saved " + orders.size() + " orders to file");

        } catch (IOException e) {
            logger.error("Failed to save orders to file", e);
            throw new RuntimeException("Cannot save orders", e);
        }
    }

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
        saveOrdersToFile();
        logger.info("Saved order: " + order.getId() + " with status: " + order.getStatus());
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String orderId) {
        Order removed = orders.remove(orderId);
        if (removed != null) {
            saveOrdersToFile();
            logger.info("Deleted order: " + orderId);
            return true;
        }
        return false;
    }

    @Override
    public boolean exists(String orderId) {
        return orders.containsKey(orderId);
    }

    @Override
    public int count() {
        return orders.size();
    }

    public Map<String, Order> getAllAsMap() {
        return new HashMap<>(orders);
    }

    private static class OrderData {
        String id;
        String description;
        OrderStatus status;
        Map<OrderStatus, java.time.LocalDateTime> stateTimestamps;
        java.time.LocalDateTime createdAt;
        java.time.LocalDateTime updatedAt;

        static OrderData fromOrder(Order order) {
            OrderData data = new OrderData();
            data.id = order.getId();
            data.description = order.getDescription();
            data.status = order.getStatus();
            data.stateTimestamps = order.getStateTimestamps();
            data.createdAt = order.getCreatedAt();
            data.updatedAt = order.getUpdatedAt();
            return data;
        }
    }
}