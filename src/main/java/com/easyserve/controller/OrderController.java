
package com.easyserve.controller;

import com.easyserve.dto.*;
import com.easyserve.model.Order;
import com.easyserve.model.Order.Status;
import com.easyserve.service.OrderService;
import com.easyserve.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest req) {
        Order order = orderService.createOrder(req.toOrderDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('STAFF')")
    public ResponseEntity<List<OrderResponse>> listOrders(
            @RequestParam UUID restaurantId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Order.OrderType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) UUID customerId) {

        List<Order> orders = orderService.filterOrders(restaurantId, status, type, from, to, customerId);
        return ResponseEntity.ok(orders.stream()
                                        .map(OrderResponse::from)
                                        .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF') or hasRole('MANAGER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderUpdateRequest req) {
        Order updated = orderService.updateOrderStatus(id, req.getStatus());
        return ResponseEntity.ok(OrderResponse.from(updated));
    }

    @GetMapping("/kitchen/active")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<KitchenOrderView>> getKitchenActiveOrders(@RequestParam UUID restaurantId) {
        List<Order> orders = orderService.getOrdersForKitchen(restaurantId);
        List<KitchenOrderView> view = orders.stream()
            .map(KitchenOrderView::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(view);
    }

    @GetMapping("/kitchen/stats")
    @PreAuthorize("hasRole('MANAGER') or hasRole('OWNER')")
    public ResponseEntity<KitchenStatsResponse> getKitchenStats(@RequestParam UUID restaurantId) {
        KitchenStatsResponse stats = orderService.calculateKitchenStats(restaurantId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(@PathVariable UUID customerId) {
        List<Order> orders = orderService.getOrderHistory(customerId);
        return ResponseEntity.ok(orders.stream()
                                        .map(OrderResponse::from)
                                        .collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('OWNER')")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        orderService.cancelOrder(id, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/menu")
    public ResponseEntity<List<MenuItemResponse>> listPublicMenu(@RequestParam UUID restaurantId) {
        List<MenuItemResponse> menu = menuItemService.getMenuForRestaurant(restaurantId);
        return ResponseEntity.ok(menu);
    }

    @PutMapping("/{id}/preparing")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> markPreparing(@PathVariable UUID id) {
        Order o = orderService.updateOrderStatus(id, Status.PREPARING);
        return ResponseEntity.ok(OrderResponse.from(o));
    }

    @PutMapping("/{id}/ready")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> markReady(@PathVariable UUID id) {
        Order o = orderService.updateOrderStatus(id, Status.READY);
        return ResponseEntity.ok(OrderResponse.from(o));
    }

    @PutMapping("/{id}/completed")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> markCompleted(@PathVariable UUID id) {
        Order o = orderService.updateOrderStatus(id, Status.COMPLETED);
        return ResponseEntity.ok(OrderResponse.from(o));
    }
}
