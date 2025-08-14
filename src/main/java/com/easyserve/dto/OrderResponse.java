

package com.easyserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String status;
    private Integer tableNumber;
    private List<String> menuItems;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private String message;
}

