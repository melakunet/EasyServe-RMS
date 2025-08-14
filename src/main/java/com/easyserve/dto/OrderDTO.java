
package com.easyserve.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDTO {

    private UUID id;

    @NotNull
    private UUID restaurantId;

    // Replace CustomerDTO with simple fields
    @NotBlank
    private String customerName;
    
    @Email
    private String customerEmail;
    
    private String customerPhone;

    @NotBlank
    @Pattern(regexp = "^(PICKUP|DELIVERY)$", message = "Order type must be PICKUP or DELIVERY")
    private String orderType;

    @NotBlank
    @Pattern(regexp = "^(NEW|CONFIRMED|PREPARING|READY|COMPLETED|CANCELLED)$", 
             message = "Status must be NEW, CONFIRMED, PREPARING, READY, COMPLETED, or CANCELLED")
    private String status;

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

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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
        return "PICKUP".equals(orderType);
    }

    public boolean isDeliveryOrder() {
        return "DELIVERY".equals(orderType);
    }

    public boolean isActive() {
        return "NEW".equals(status) || "CONFIRMED".equals(status) || "PREPARING".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status) || "CANCELLED".equals(status);
    }

    // Nested OrderItemDTO class with getters/setters
    public static class OrderItemDTO {

        @NotNull
        private UUID menuItemId;

        @NotBlank
        private String itemName;

        @Min(1)
        private int quantity;

        @DecimalMin("0.00")
        private BigDecimal unitPrice;

        @DecimalMin("0.00")
        private BigDecimal totalPrice;

        @Size(max = 250)
        private String specialRequests;

        // Constructors
        public OrderItemDTO() {}

        public OrderItemDTO(UUID menuItemId, String itemName, int quantity, BigDecimal unitPrice) {
            this.menuItemId = menuItemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }

        // Getters and Setters
        public UUID getMenuItemId() { return menuItemId; }
        public void setMenuItemId(UUID menuItemId) { this.menuItemId = menuItemId; }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

        public String getSpecialRequests() { return specialRequests; }
        public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

        // Calculate total price
        public void calculateTotalPrice() {
            if (unitPrice != null && quantity > 0) {
                this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            }
        }
    }
}