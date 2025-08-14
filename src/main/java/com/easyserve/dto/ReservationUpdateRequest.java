

package com.easyserve.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationUpdateRequest {

    @Future(message = "Reservation date must be in the future")
    private LocalDate reservationDate;

    @NotNull(message = "Reservation time is required")
    private LocalTime reservationTime;

    @Min(value = 1, message = "Party size must be at least 1")
    @Max(value = 20, message = "Party size cannot exceed 20")
    private Integer partySize;

    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;

    @Pattern(regexp = "^(CONFIRMED|SEATED|COMPLETED|CANCELLED|NO_SHOW)$", 
             message = "Status must be one of: CONFIRMED, SEATED, COMPLETED, CANCELLED, NO_SHOW")
    private String status;

    // Business logic methods
    public boolean isStatusChange() {
        return status != null && !status.trim().isEmpty();
    }

    public boolean isTimeChange() {
        return reservationDate != null || reservationTime != null;
    }

    public boolean isValidForUpdate() {
        return reservationDate != null || reservationTime != null || 
               partySize != null || specialRequests != null || status != null;
    }

    public boolean isCompletedStatus() {
        return "COMPLETED".equals(status) || "CANCELLED".equals(status) || "NO_SHOW".equals(status);
    }

    public boolean isActiveStatus() {
        return "CONFIRMED".equals(status) || "SEATED".equals(status);
    }

    // Helper method to validate business rules
    public boolean canModifyReservation() {
        // Can't modify if status is being changed to completed states
        if (isCompletedStatus()) {
            return false;
        }
        // Allow modifications for active states
        return isActiveStatus() || status == null;
    }

    // Method to check if this is just a status update
    public boolean isStatusOnlyUpdate() {
        return status != null && reservationDate == null && 
               reservationTime == null && partySize == null && 
               (specialRequests == null || specialRequests.trim().isEmpty());
    }
}
