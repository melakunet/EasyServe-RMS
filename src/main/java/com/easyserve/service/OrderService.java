
package com.easyserve.service;


import com.easyserve.dto.*;
import com.easyserve.dto.OrderDTO.OrderItemDTO;
import com.easyserve.model.OrderStatus;
import com.easyserve.model.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.time.Duration;


@Service
public class OrderService {

    @Autowired
    private NotificationService notificationService;

    // Mock storage for orders (in-memory for MVP)
    private final Map<Long, OrderDTO> orders = new ConcurrentHashMap<>();
    private final AtomicLong orderIdGenerator = new AtomicLong(1);
    
    // Mock menu items with prices
    private final Map<Long, BigDecimal> menuItemPrices = new ConcurrentHashMap<>();

    public OrderService() {
        // Initialize some mock menu items for testing
        menuItemPrices.put(1L, new BigDecimal("12.99")); // Burger
        menuItemPrices.put(2L, new BigDecimal("8.99"));  // Pizza
        menuItemPrices.put(3L, new BigDecimal("6.99"));  // Salad
        menuItemPrices.put(4L, new BigDecimal("4.99"));  // Fries
        menuItemPrices.put(5L, new BigDecimal("2.99"));  // Drink
    }

    public OrderDTO createOrder(OrderDTO dto) {
        // Generate new order ID
        Long orderId = orderIdGenerator.getAndIncrement();
        dto.setId(orderId);
        
        // Calculate pricing
        BigDecimal subtotal = calculateSubtotal(dto.getItems());
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.10")); // 10% tax
        BigDecimal total = subtotal.add(tax);
        
        // Set calculated values
        dto.setSubtotal(subtotal);
        dto.setTax(tax);
        dto.setTotal(total);
        dto.setStatus(OrderStatus.PENDING);
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

    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
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
            newStatus.toString()
        );

        return order;
    }

    public List<OrderDTO> filterOrders(Long restaurantId, OrderStatus status, OrderType type, 
                                      LocalDateTime from, LocalDateTime to, Long customerId) {
        return orders.values().stream()
                .filter(order -> restaurantId == null || order.getRestaurantId().equals(restaurantId))
                .filter(order -> status == null || order.getStatus().equals(status))
                .filter(order -> type == null || order.getOrderType().equals(type))
                .filter(order -> from == null || order.getCreatedAt().isAfter(from))
                .filter(order -> to == null || order.getCreatedAt().isBefore(to))
                .filter(order -> customerId == null || order.getCustomerId().equals(customerId))
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .toList();
    }

    public List<OrderDTO> getOrdersForKitchen(Long restaurantId) {
        // Return active orders for kitchen display
        return orders.values().stream()
                .filter(order -> order.getRestaurantId().equals(restaurantId))
                .filter(order -> isActiveStatus(order.getStatus()))
                .sorted((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
                .toList();
    }

    public KitchenStatsResponse calculateKitchenStats(Long restaurantId) {
        List<OrderDTO> todayOrders = getTodaysOrders(restaurantId);
        
        int totalActive = (int) todayOrders.stream()
                .filter(order -> isActiveStatus(order.getStatus()))
                .count();
        
        int preparing = (int) todayOrders.stream()
                .filter(order -> OrderStatus.PREPARING.equals(order.getStatus()))
                .count();
        
        int ready = (int) todayOrders.stream()
                .filter(order -> OrderStatus.READY.equals(order.getStatus()))
                .count();
        
        int completed = (int) todayOrders.stream()
                .filter(order -> OrderStatus.COMPLETED.equals(order.getStatus()))
                .count();
        
        // Simple average preparation time calculation
 double avgPrepTime = 25.0; // Mock value

KitchenStatsResponse response = new KitchenStatsResponse();
response.setTotalActiveOrders(totalActive);
response.setOrdersInPreparation(preparing);
response.setOrdersReady(ready);
response.setOrdersCompleted(completed);
response.setAveragePreparationTime(Duration.ofMinutes((long) avgPrepTime)); //  Fixed
response.setTotalOrdersToday(todayOrders.size());
return response;
    }


    public List<OrderDTO> getOrderHistory(Long customerId) {
        // Mock customer order history by customer ID
        return orders.values().stream()
                .filter(order -> customerId.equals(order.getCustomerId()))
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .toList();
    }

    public void cancelOrder(Long orderId, String reason) {
        OrderDTO order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        order.setStatus(OrderStatus.CANCELLED);
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

    public OrderDTO getOrderById(Long orderId) {
        OrderDTO order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return order;
    }

    public List<OrderDTO> getTodaysOrders(Long restaurantId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        return orders.values().stream()
                .filter(order -> order.getRestaurantId().equals(restaurantId))
                .filter(order -> order.getCreatedAt().isAfter(startOfDay) && 
                               order.getCreatedAt().isBefore(endOfDay))
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .toList();
    }

    public BigDecimal calculateSubtotal(List<OrderItemDTO> items) {
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

    // Business helper methods
    private boolean isActiveStatus(OrderStatus status) {
        return OrderStatus.PENDING.equals(status) || 
               OrderStatus.CONFIRMED.equals(status) || 
               OrderStatus.PREPARING.equals(status);
    }

    public boolean canCancelOrder(Long orderId) {
        OrderDTO order = orders.get(orderId);
        return order != null && 
               (OrderStatus.PENDING.equals(order.getStatus()) || OrderStatus.CONFIRMED.equals(order.getStatus()));
    }

    public void markOrderReady(Long orderId) {
        updateOrderStatus(orderId, OrderStatus.READY);
        
        OrderDTO order = orders.get(orderId);
        if (order != null) {
            notificationService.sendOrderReadyNotification(
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                orderId,
                order.getOrderType().toString()
            );
        }
    }

    public void completeOrder(Long orderId) {
        updateOrderStatus(orderId, OrderStatus.COMPLETED);
    }

    // Analytics methods
    public int getTotalOrdersToday(Long restaurantId) {
        return getTodaysOrders(restaurantId).size();
    }

    public BigDecimal getTotalRevenueToday(Long restaurantId) {
        return getTodaysOrders(restaurantId).stream()
                .filter(order -> OrderStatus.COMPLETED.equals(order.getStatus()))
                .map(OrderDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getAverageOrderValue(Long restaurantId) {
        List<OrderDTO> completedOrders = orders.values().stream()
                .filter(order -> order.getRestaurantId().equals(restaurantId))
                .filter(order -> OrderStatus.COMPLETED.equals(order.getStatus()))
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