
package com.easyserve.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public class MenuItemDTO {

    private UUID id;

    @NotBlank
    private String name;

    @DecimalMin("0.00")
    private BigDecimal price;

    @Size(max = 500)
    private String description;

    private boolean available;

    // Getters and setters
}

