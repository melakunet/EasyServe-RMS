
package com.easyserve.dto;

import com.easyserve.model.enums.OrderStatus;
import com.easyserve.model.enums.OrderType;
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

    @NotNull
    @Valid
    private CustomerDTO customerInfo;

    @NotNull
    private OrderType orderType;

    @NotNull
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

    // Getters and setters

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

        // Getters and setters
    }
}
