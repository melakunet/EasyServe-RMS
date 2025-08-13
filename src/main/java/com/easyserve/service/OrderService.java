
package com.easyserve.service;

import com.easyserve.dto.OrderDTO;
import com.easyserve.model.*;
import com.easyserve.model.Order.OrderType;
import com.easyserve.model.Order.Status;
import com.easyserve.repository.CustomerRepository;
import com.easyserve.repository.MenuItemRepository;
import com.easyserve.repository.OrderRepository;
import com.easyserve.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    private final NotificationService notificationService;

    public Order createOrder(OrderDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        BigDecimal subtotal = calculateSubtotal(dto.getMenuItems());
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.10")); // example: 10% tax
        BigDecimal total = subtotal.add(tax);

        Order order = Order.builder()
                .restaurant(restaurant)
                .customer(customer)
                .orderType(dto.getOrderType())
                .status(Status.NEW)
                .subtotal(subtotal)
                .tax(tax)
                .total(total)
                .estimatedTime(LocalDateTime.now().plusMinutes(30)) // basic estimate
                .specialInstructions(dto.getSpecialInstructions())
                .deliveryAddress(dto.getDeliveryAddress())
                .build();

        Order saved = orderRepository.save(order);
        notificationService.sendOrderConfirmation(saved);
        return saved;
    }

    public Order updateOrderStatus(UUID orderId, Status newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersForKitchen(UUID restaurantId) {
        return orderRepository.findActiveKitchenOrders(restaurantId);
    }

    public BigDecimal calculateSubtotal(List<UUID> menuItemIds) {
        return menuItemIds.stream()
                .map(id -> menuItemRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id)))
                .map(MenuItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Order> getOrderHistory(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public void cancelOrder(UUID orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(Status.CANCELLED);
        order.setSpecialInstructions((order.getSpecialInstructions() != null ? order.getSpecialInstructions() + " | " : "") + "Cancelled: " + reason);
        orderRepository.save(order);

        notificationService.sendOrderCancellation(order);
    }
}
