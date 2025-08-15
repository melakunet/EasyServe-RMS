
package com.easyserve.dto;

import com.easyserve.model.OrderStatus;
import com.easyserve.model.OrderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    private Long id;

    @NotNull
    private Long restaurantId;

    // Customer fields
    @NotNull
    private Long customerId;

    @NotBlank
    private String customerName;
    
    @Email
    private String customerEmail;
    
    private String customerPhone;

    @NotNull(message = "Order type is required")
    private OrderType orderType;

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    @NotEmpty
    @Valid
    private List<OrderItemDTO> items;

    @DecimalMin("0.00")
    private BigDecimal subtotal;

    @DecimalMin("0.00")
    private BigDecimal tax;

    @DecimalMin("0.00")
    private BigDecimal total;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedTime;

    @Size(max = 500)
    private String specialInstructions;

    private String deliveryAddress;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public OrderDTO() {}

    public OrderDTO(Long restaurantId, Long customerId, String customerName, String customerEmail, 
                   String customerPhone, OrderType orderType) {
        this.restaurantId = restaurantId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.orderType = orderType;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public OrderType getOrderType() { return orderType; }
    public void setOrderType(OrderType orderType) { this.orderType = orderType; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDateTime getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(LocalDateTime estimatedTime) { this.estimatedTime = estimatedTime; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business logic methods
    public boolean isPickupOrder() {
        return OrderType.PICKUP.equals(orderType);
    }

    public boolean isDeliveryOrder() {
        return OrderType.DELIVERY.equals(orderType);
    }

    public boolean isDineInOrder() {
        return OrderType.DINE_IN.equals(orderType);
    }

    public boolean isActive() {
        return OrderStatus.PENDING.equals(status) || 
               OrderStatus.CONFIRMED.equals(status) || 
               OrderStatus.PREPARING.equals(status);
    }

    public boolean isCompleted() {
        return OrderStatus.COMPLETED.equals(status) || 
               OrderStatus.CANCELLED.equals(status);
    }

    public boolean canBeCancelled() {
        return OrderStatus.PENDING.equals(status) || 
               OrderStatus.CONFIRMED.equals(status);
    }

    public int getTotalItems() {
        return items != null ? items.stream().mapToInt(OrderItemDTO::getQuantity).sum() : 0;
    }

    // Nested OrderItemDTO class
    public static class OrderItemDTO {

        @NotNull
        private Long menuItemId;

        @NotBlank
        private String itemName;

        @Min(1)
        private Integer quantity;

        @DecimalMin("0.00")
        private BigDecimal unitPrice;

        @DecimalMin("0.00")
        private BigDecimal totalPrice;

        @Size(max = 250)
        private String specialRequests;

        // Constructors
        public OrderItemDTO() {}

        public OrderItemDTO(Long menuItemId, String itemName, Integer quantity, BigDecimal unitPrice) {
            this.menuItemId = menuItemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }

        // Getters and Setters
        public Long getMenuItemId() { return menuItemId; }
        public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

        public String getSpecialRequests() { return specialRequests; }
        public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

        // Calculate total price
        public void calculateTotalPrice() {
            if (unitPrice != null && quantity != null && quantity > 0) {
                this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            }
        }
    }
}