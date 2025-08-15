package com.easyserve.controller;

import com.easyserve.dto.*;
import com.easyserve.model.Order;
import com.easyserve.model.OrderStatus;
import com.easyserve.model.OrderType;
import com.easyserve.service.OrderService;
import com.easyserve.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private MenuItemService menuItemService;

    // Create new order
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderCreateRequest req) {
        // Convert OrderCreateRequest to OrderDTO
        OrderDTO orderDTO = convertToOrderDTO(req);
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(createdOrder);
    }

    // List/filter orders
    @GetMapping
    public ResponseEntity<List<OrderDTO>> listOrders(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) OrderType type,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) Long customerId) {
        
        List<OrderDTO> orders = orderService.filterOrders(restaurantId, status, type, from, to, customerId);
        return ResponseEntity.ok(orders);
    }

    // Get specific order by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    // Update order status (generic)
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    // Mark order as preparing
    @PutMapping("/{id}/preparing")
    public ResponseEntity<OrderDTO> markPreparing(@PathVariable Long id) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, OrderStatus.PREPARING);
        return ResponseEntity.ok(updatedOrder);
    }

    // Mark order as ready
    @PutMapping("/{id}/ready")
    public ResponseEntity<OrderDTO> markReady(@PathVariable Long id) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, OrderStatus.READY);
        return ResponseEntity.ok(updatedOrder);
    }

    // Mark order as completed
    @PutMapping("/{id}/completed")
    public ResponseEntity<OrderDTO> markCompleted(@PathVariable Long id) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, OrderStatus.COMPLETED);
        return ResponseEntity.ok(updatedOrder);
    }

    // Get active orders for kitchen
    @GetMapping("/kitchen/active")
    public ResponseEntity<List<OrderDTO>> getKitchenActiveOrders(@RequestParam Long restaurantId) {
        List<OrderDTO> activeOrders = orderService.getOrdersForKitchen(restaurantId);
        return ResponseEntity.ok(activeOrders);
    }

    // Get kitchen statistics
    @GetMapping("/kitchen/stats")
    public ResponseEntity<KitchenStatsResponse> getKitchenStats(@RequestParam Long restaurantId) {
        KitchenStatsResponse stats = orderService.calculateKitchenStats(restaurantId);
        return ResponseEntity.ok(stats);
    }

    // Get customer order history
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(@PathVariable Long customerId) {
        List<OrderDTO> orderHistory = orderService.getOrderHistory(customerId);
        return ResponseEntity.ok(orderHistory);
    }

    // Cancel order
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id, @RequestParam String reason) {
        orderService.cancelOrder(id, reason);
        return ResponseEntity.ok().build();
    }

    // Get public menu for a restaurant
    @GetMapping("/menu/public")
    public ResponseEntity<List<MenuItemResponse>> listPublicMenu(@RequestParam Long restaurantId) {
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