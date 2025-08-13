package com.easyserve.dto;

import com.easyserve.model.enums.ReservationSource;
import com.easyserve.model.enums.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class ReservationDTO {

    private UUID id;

    @NotNull
    private UUID restaurantId;

    @NotBlank
    private String customerFirstName;

    @NotBlank
    private String customerLastName;

    @Email
    @NotBlank
    private String customerEmail;

    @Pattern(regexp = "\\+?[0-9. ()-]{7,25}", message = "Invalid phone number")
    @NotBlank
    private String customerPhone;

    @Future
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationDate;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime reservationTime;

    @NotNull
    @Min(1)
    @Max(20)
    private Integer partySize;

    @Size(max = 500)
    private String specialRequests;

    @NotNull
    private ReservationStatus status;

    @NotNull
    private ReservationSource source;

    // Getters and setters
}
