package com.wolt.ui;

import com.wolt.model.Order;
import com.wolt.model.OrderStatus;
import com.wolt.service.OrderService;


import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final OrderService orderService;
    private final Scanner scanner;

    public ConsoleUI() {
        this.orderService = new OrderService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        System.out.println("=== Food Delivery Resilient System ===");

        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> createOrder();
                case "2" -> findOrder();
                case "3" -> updateOrderStatus();
                case "4" -> listAllOrders();
                case "5" -> showCacheStats();
                case "6" -> clearCache();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Create New Order ");
        System.out.println("2. Find Order by ID ");
        System.out.println("3. Update Order Status ");
        System.out.println("4. List All Orders ");
        System.out.println("5. Show Cache Statistics (Audit)");
        System.out.println("6. CLEAR CACHE ");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    private void createOrder() {
        System.out.print("Enter food description: ");
        String desc = scanner.nextLine();
        try {
            Order order = orderService.createOrder(desc);
            System.out.println("Order created successfully! ID: " + order.getId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void findOrder() {
        System.out.print("Enter Order ID: ");
        String id = scanner.nextLine();

        orderService.findOrderById(id).ifPresentOrElse(
                order -> {
                    System.out.println("\n[SYSTEM] Order Found: " + order);
                    System.out.println("[SYSTEM] Status: " + order.getStatus());
                    System.out.println("[AUDIT] " + orderService.getCacheStats());
                },
                () -> System.out.println("Order not found.")
        );
    }

    private void updateOrderStatus() {
        System.out.print("Enter Order ID: ");
        String id = scanner.nextLine();

        System.out.println("Select New Status:");
        System.out.println("1. CONFIRMED (Stage 2)");
        System.out.println("2. DELIVERED (Stage 3 - Terminal)");
        System.out.print("Choice: ");

        String choice = scanner.nextLine();
        OrderStatus newStatus = choice.equals("1") ? OrderStatus.CONFIRMED : OrderStatus.DELIVERED;

        try {
            orderService.updateOrderStatus(id, newStatus);
            System.out.println("Status updated to " + newStatus);
        } catch (Exception e) {
            System.out.println("\n!!! LOGIC ERROR: " + e.getMessage());
        }
    }

    private void listAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders in the system.");
        } else {
            orders.forEach(System.out::println);
        }
    }

    private void showCacheStats() {
        System.out.println("\n" + orderService.getCacheStats());
    }
    private void clearCache() {
        orderService.clearCache();
        System.out.println("[SYSTEM] Cache has been cleared!");
    }
}