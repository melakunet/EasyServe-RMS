
package com.easyserve.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservationDTO {

    private Long id;

    @NotNull
    private Long restaurantId;

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

    @NotBlank
    @Pattern(regexp = "^(CONFIRMED|SEATED|COMPLETED|CANCELLED|NO_SHOW)$", 
             message = "Status must be CONFIRMED, SEATED, COMPLETED, CANCELLED, or NO_SHOW")
    private String status;

    @NotBlank
    @Pattern(regexp = "^(ONLINE|PHONE|WALK_IN)$", 
             message = "Source must be ONLINE, PHONE, or WALK_IN")
    private String source;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public ReservationDTO() {}

    public ReservationDTO(Long restaurantId, String customerFirstName, String customerLastName,
                         String customerEmail, String customerPhone, LocalDate reservationDate,
                         LocalTime reservationTime, Integer partySize) {
        this.restaurantId = restaurantId;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.partySize = partySize;
        this.status = "CONFIRMED";
        this.source = "ONLINE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public String getCustomerFirstName() { return customerFirstName; }
    public void setCustomerFirstName(String customerFirstName) { this.customerFirstName = customerFirstName; }

    public String getCustomerLastName() { return customerLastName; }
    public void setCustomerLastName(String customerLastName) { this.customerLastName = customerLastName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public LocalDate getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDate reservationDate) { this.reservationDate = reservationDate; }

    public LocalTime getReservationTime() { return reservationTime; }
    public void setReservationTime(LocalTime reservationTime) { this.reservationTime = reservationTime; }

    public Integer getPartySize() { return partySize; }
    public void setPartySize(Integer partySize) { this.partySize = partySize; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business Logic Methods
    public String getCustomerFullName() {
        return customerFirstName + " " + customerLastName;
    }

    public boolean isActive() {
        return "CONFIRMED".equals(status) || "SEATED".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status) || "CANCELLED".equals(status) || "NO_SHOW".equals(status);
    }

    public boolean isOnlineBooking() {
        return "ONLINE".equals(source);
    }

    public boolean isWalkIn() {
        return "WALK_IN".equals(source);
    }

    public boolean isTodayReservation() {
        return reservationDate != null && reservationDate.equals(LocalDate.now());
    }

    public String getReservationTimeSlot() {
        if (reservationDate == null || reservationTime == null) return null;
        return reservationDate + " at " + reservationTime;
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValidReservation() {
        return reservationDate != null && 
               reservationTime != null && 
               partySize != null && partySize > 0 && partySize <= 20 &&
               customerEmail != null && customerEmail.contains("@");
    }

    @Override
    public String toString() {
        return "ReservationDTO{" +
                "id=" + id +
                ", restaurantId=" + restaurantId +
                ", customerName='" + getCustomerFullName() + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", reservationDate=" + reservationDate +
                ", reservationTime=" + reservationTime +
                ", partySize=" + partySize +
                ", status='" + status + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}