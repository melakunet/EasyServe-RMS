
package com.easyserve.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class RestaurantDTO {

    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String address;

    @Size(max = 15)
    private String phoneNumber;

    // Getters and setters
}
