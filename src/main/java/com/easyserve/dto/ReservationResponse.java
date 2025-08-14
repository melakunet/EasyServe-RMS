
package com.easyserve.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("restaurant_id")
    private UUID restaurantId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("customer_phone")
    private String customerPhone;

    @JsonProperty("reservation_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationDate;

    @JsonProperty("reservation_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime reservationTime;

    @JsonProperty("party_size")
    private Integer partySize;

    @JsonProperty("status")
    private String status;

    @JsonProperty("special_requests")
    private String specialRequests;

    @JsonProperty("source")
    private String source;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Utility methods
    public boolean isActive() {
        return "CONFIRMED".equals(status) || "SEATED".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status) || "CANCELLED".equals(status);
    }

    public String getFormattedDateTime() {
        return reservationDate + " at " + reservationTime;
    }
}