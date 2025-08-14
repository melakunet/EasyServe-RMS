
package com.easyserve.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderUpdateRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Order status is required")
    private String status; // e.g., "IN_PROGRESS", "COMPLETED"
}
