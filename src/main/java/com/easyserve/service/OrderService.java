
package com.easyserve.service;

import com.easyserve.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    @Autowired
    private NotificationService notificationService;

    // Mock storage for orders (in-memory for MVP)
    private final Map<UUID, OrderDTO> orders = new ConcurrentHashMap<>();
    
    // Mock menu items with prices
    private final Map<UUID, BigDecimal> menuItemPrices = new ConcurrentHashMap<>();

    public OrderService() {
        // Initialize some mock menu items for testing
        menuItemPrices.put(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), new BigDecimal("12.99")); // Burger
        menuItemPrices.put(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"), new BigDecimal("8.99"));  // Pizza
        menuItemPrices.put(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"), new BigDecimal("6.99"));  // Salad
        menuItemPrices.put(UUID.fromString("550e8400-e29b-41d4-a716-446655440004"), new BigDecimal("4.99"));  // Fries
        menuItemPrices.put(UUID.fromString("550e8400-e29b-41d4-a716-446655440005"), new BigDecimal("2.99"));  // Drink
    }

    public OrderDTO createOrder(OrderDTO dto) {
        // Generate new order ID
        UUID orderId = UUID.randomUUID();
        dto.setId(orderId);
        
        // Calculate pricing
        BigDecimal subtotal = calculateSubtotal(dto.getItems());
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.10")); // 10% tax
        BigDecimal total = subtotal.add(tax);
        
        // Set calculated values
        dto.setSubtotal(subtotal);
        dto.setTax(tax);
        dto.setTotal(total);
        dto.setStatus("NEW");
        dto.setEstimatedTime(LocalDateTime.now().plusMinutes(30));
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        // Save order
        orders.put(orderId, dto);

        // Send confirmation notification
        notificationService.sendOrderStatusUpdate(
            dto.getCustomerEmail(), 
            dto.getCustomerPhone(), 
            orderId, 
            "CONFIRMED"
        );

        return dto;
    }

    public OrderDTO updateOrderStatus(UUID orderId, String newStatus) {
        OrderDTO order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        // Send status update notification
        notificationService.sendOrderStatusUpdate(
            order.getCustomerEmail(),
            order.getCustomerPhone(),
            orderId,
            newStatus
        );

        return order;
    }

    public List<OrderDTO> getOrdersForKitchen(UUID restaurantId) {
        // Return active orders for kitchen display
        return orders.values().stream()
                .filter(order -> order.getRestaurantId().equals(restaurantId))
                .filter(order -> isActiveStatus(order.getStatus()))
                .sorted((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
                .toList();
    }

    public List<OrderDTO> getOrderHistory(UUID customerId, String customerEmail) {
        // Mock customer order history by email
        return orders.values().stream()
                .filter(order -> customerEmail.equals(order.getCustomerEmail()))
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .toList();
    }

    public void cancelOrder(UUID orderId, String reason) {
        OrderDTO order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        order.setStatus("CANCELLED");
        order.setSpecialInstructions(
            (order.getSpecialInstructions() != null ? order.getSpecialInstructions() + " | " : "") + 
            "Cancelled: " + reason
        );
        order.setUpdatedAt(LocalDateTime.now());

        // Send cancellation notification
        notificationService.sendOrderStatusUpdate(
            order.getCustomerEmail(),
            order.getCustomerPhone(),
            orderId,
            "CANCELLED"
        );
    }

    public OrderDTO getOrderById(UUID orderId) {
        OrderDTO order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return order;
    }

    public List<OrderDTO> getTodaysOrders(UUID restaurantId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        return orders.values().stream()
                .filter(order -> order.getRestaurantId().equals(restaurantId))
                .filter(order -> order.getCreatedAt().isAfter(startOfDay) && 
                               order.getCreatedAt().isBefore(endOfDay))
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .toList();
    }

    public BigDecimal calculateSubtotal(List<OrderDTO.OrderItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .map(item -> {
                    BigDecimal price = menuItemPrices.getOrDefault(item.getMenuItemId(), new BigDecimal("9.99"));
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<OrderDTO> getOrdersByStatus(UUID restaurantId, String status) {
        return orders.values().stream()
                .filter(order -> order.getRestaurantId().equals(restaurantId))
                .filter(order -> status.equals(order.getStatus()))
                .sorted((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
                .toList();
    }

    // Business helper methods
    private boolean isActiveStatus(String status) {
        return "NEW".equals(status) || 
               "CONFIRMED".equals(status) || 
               "PREPARING".equals(status);
    }

    public boolean canCancelOrder(UUID orderId) {
        OrderDTO order = orders.get(orderId);
        return order != null && 
               ("NEW".equals(order.getStatus()) || "CONFIRMED".equals(order.getStatus()));
    }

    public void markOrderReady(UUID orderId) {
        updateOrderStatus(orderId, "READY");
        
        OrderDTO order = orders.get(orderId);
        if (order != null) {
            notificationService.sendOrderReadyNotification(
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                orderId,
                order.getOrderType()
            );
        }
    }

    public void completeOrder(UUID orderId) {
        updateOrderStatus(orderId, "COMPLETED");
    }

    // Analytics methods
    public int getTotalOrdersToday(UUID restaurantId) {
        return getTodaysOrders(restaurantId).size();
    }

    public BigDecimal getTotalRevenueToday(UUID restaurantId) {
        return getTodaysOrders(restaurantId).stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .map(OrderDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getAverageOrderValue(UUID restaurantId) {
        List<OrderDTO> completedOrders = orders.values().stream()
                .filter(order -> order.getRestaurantId().equals(restaurantId))
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .toList();

        if (completedOrders.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalRevenue = completedOrders.stream()
                .map(OrderDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalRevenue.divide(BigDecimal.valueOf(completedOrders.size()), 2, BigDecimal.ROUND_HALF_UP);
    }
}