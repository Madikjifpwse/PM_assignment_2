package com.wolt.repository;

import com.wolt.model.Order;
import com.wolt.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    void save(Order order);
    Optional<Order> findById(String orderId);
    List<Order> findAll();
    List<Order> findByStatus(OrderStatus status);
    boolean delete(String orderId);
    boolean exists(String orderId);
    int count();
}