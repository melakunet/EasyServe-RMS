
package com.easyserve.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AvailabilityResponse {

    private boolean available;
    private String message;
    private LocalDate date;
    private List<LocalTime> availableTimeSlots;
    private int maxPartySize;
    private List<String> unavailableReasons;

    // Default constructor
    public AvailabilityResponse() {}

    // Constructor for available response
    public AvailabilityResponse(boolean available, String message, LocalDate date, List<LocalTime> availableTimeSlots) {
        this.available = available;
        this.message = message;
        this.date = date;
        this.availableTimeSlots = availableTimeSlots;
    }

    // Constructor for unavailable response
    public AvailabilityResponse(boolean available, String message, List<String> unavailableReasons) {
        this.available = available;
        this.message = message;
        this.unavailableReasons = unavailableReasons;
    }

    // Static factory methods for common responses
    public static AvailabilityResponse available(LocalDate date, List<LocalTime> timeSlots) {
        return new AvailabilityResponse(true, "Tables available", date, timeSlots);
    }

    public static AvailabilityResponse unavailable(String reason) {
        return new AvailabilityResponse(false, reason, List.of(reason));
    }

    public static AvailabilityResponse unavailable(List<String> reasons) {
        return new AvailabilityResponse(false, "No tables available", reasons);
    }

    // Getters and Setters
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<LocalTime> getAvailableTimeSlots() {
        return availableTimeSlots;
    }

    public void setAvailableTimeSlots(List<LocalTime> availableTimeSlots) {
        this.availableTimeSlots = availableTimeSlots;
    }

    public int getMaxPartySize() {
        return maxPartySize;
    }

    public void setMaxPartySize(int maxPartySize) {
        this.maxPartySize = maxPartySize;
    }

    public List<String> getUnavailableReasons() {
        return unavailableReasons;
    }

    public void setUnavailableReasons(List<String> unavailableReasons) {
        this.unavailableReasons = unavailableReasons;
    }

    // Helper methods
    public boolean hasTimeSlots() {
        return availableTimeSlots != null && !availableTimeSlots.isEmpty();
    }

    public int getAvailableSlotCount() {
        return availableTimeSlots != null ? availableTimeSlots.size() : 0;
    }
}
