package com.easyserve.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationCreateRequest {

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Email(message = "Valid email is required")
    private String customerEmail;

    @NotBlank(message = "Phone number is required")
    private String customerPhone;

    @NotNull(message = "Reservation date and time is required")
    private LocalDateTime reservationDateTime;

    @Min(value = 1, message = "Party size must be at least 1")
    private int partySize;

    private String specialRequests;
}
