
package com.easyserve.controller;

import com.easyserve.dto.*;
import com.easyserve.model.Order;
import com.easyserve.model.Order.Status;
import com.easyserve.service.OrderService;
import com.easyserve.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private MenuItemService menuItemService;

    // Create new order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreateRequest req) {
        // Convert OrderCreateRequest to OrderDTO
        OrderDTO orderDTO = convertToOrderDTO(req);
        Order createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(createdOrder);
    }

    // List/filter orders
    @GetMapping
    public ResponseEntity<List<Order>> listOrders(
            @RequestParam(required = false) UUID restaurantId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Order.OrderType type,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) UUID customerId) {
        
        List<Order> orders = orderService.filterOrders(restaurantId, status, type, from, to, customerId);
        return ResponseEntity.ok(orders);
    }

    // Get specific order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    // Update order status (generic)
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable UUID id, @RequestParam Status status) {
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    // Mark order as preparing
    @PutMapping("/{id}/preparing")
    public ResponseEntity<Order> markPreparing(@PathVariable UUID id) {
        Order updatedOrder = orderService.updateOrderStatus(id, Status.PREPARING);
        return ResponseEntity.ok(updatedOrder);
    }

    // Mark order as ready
    @PutMapping("/{id}/ready")
    public ResponseEntity<Order> markReady(@PathVariable UUID id) {
        Order updatedOrder = orderService.updateOrderStatus(id, Status.READY);
        return ResponseEntity.ok(updatedOrder);
    }

    // Mark order as completed
    @PutMapping("/{id}/completed")
    public ResponseEntity<Order> markCompleted(@PathVariable UUID id) {
        Order updatedOrder = orderService.updateOrderStatus(id, Status.COMPLETED);
        return ResponseEntity.ok(updatedOrder);
    }

    // Get active orders for kitchen
    @GetMapping("/kitchen/active")
    public ResponseEntity<List<Order>> getKitchenActiveOrders(@RequestParam UUID restaurantId) {
        List<Order> activeOrders = orderService.getOrdersForKitchen(restaurantId);
        return ResponseEntity.ok(activeOrders);
    }

    // Get kitchen statistics
    @GetMapping("/kitchen/stats")
    public ResponseEntity<KitchenStatsResponse> getKitchenStats(@RequestParam UUID restaurantId) {
        KitchenStatsResponse stats = orderService.calculateKitchenStats(restaurantId);
        return ResponseEntity.ok(stats);
    }

    // Get customer order history
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getCustomerOrders(@PathVariable UUID customerId) {
        List<Order> orderHistory = orderService.getOrderHistory(customerId);
        return ResponseEntity.ok(orderHistory);
    }

    // Cancel order
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable UUID id, @RequestParam String reason) {
        orderService.cancelOrder(id, reason);
        return ResponseEntity.ok().build();
    }

    // Get public menu for a restaurant
    @GetMapping("/menu/public")
    public ResponseEntity<List<MenuItemResponse>> listPublicMenu(@RequestParam UUID restaurantId) {
        List<MenuItemResponse> menuItems = menuItemService.getMenuForRestaurant(restaurantId);
        return ResponseEntity.ok(menuItems);
    }

    // Helper method to convert OrderCreateRequest to OrderDTO
    private OrderDTO convertToOrderDTO(OrderCreateRequest request) {
        // This is a placeholder - you'll need to implement the actual conversion
        // based on your OrderCreateRequest and OrderDTO structures
        OrderDTO orderDTO = new OrderDTO();
        // Set fields from request to orderDTO
        // orderDTO.setRestaurantId(request.getRestaurantId());
        // orderDTO.setCustomerId(request.getCustomerId());
        // etc.
        return orderDTO;
    }
}