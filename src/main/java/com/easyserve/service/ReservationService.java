
package com.easyserve.service;

import com.easyserve.dto.AvailabilityResponse;
import com.easyserve.dto.ReservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDateTime;

@Service
public class ReservationService {

    @Autowired
    private NotificationService notificationService;

    // Mock storage for reservations (in-memory for MVP)
    private final Map<Long, ReservationDTO> reservations = new ConcurrentHashMap<>();
    private final AtomicLong reservationIdGenerator = new AtomicLong(1);

    public ReservationDTO createReservation(ReservationDTO dto) {
        // Check availability
        if (!checkAvailability(dto.getRestaurantId(), dto.getReservationDate(), dto.getReservationTime())) {
            throw new IllegalStateException("Time slot not available.");
        }

        // Generate new reservation ID
        Long reservationId = reservationIdGenerator.getAndIncrement();
        dto.setId(reservationId);
        dto.setStatus("CONFIRMED");
        dto.setSource("ONLINE");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        // Save reservation
        reservations.put(reservationId, dto);

        // Send confirmation notification
        notificationService.sendReservationConfirmation(
            dto.getCustomerEmail(),
            dto.getCustomerFullName(),
            "Restaurant Name", // Mock restaurant name
            dto.getReservationDate(),
            dto.getReservationTime()
        );

        return dto;
    }

    public boolean checkAvailability(Long restaurantId, LocalDate date, LocalTime time) {
        // Check if any existing reservation conflicts with the requested time
        return reservations.values().stream()
                .filter(r -> r.getRestaurantId().equals(restaurantId))
                .filter(r -> r.getReservationDate().equals(date))
                .filter(r -> r.isActive()) // Only check active reservations
                .noneMatch(r -> isTimeConflict(r.getReservationTime(), time));
    }

    public AvailabilityResponse checkAvailability(Long restaurantId, LocalDate date, LocalTime time, Integer partySize) {
        boolean available = checkAvailability(restaurantId, date, time);
        if (available) {
            return AvailabilityResponse.available(date, List.of(time));
        } else {
            return AvailabilityResponse.unavailable("Time slot not available");
        }
    }

    public Page<ReservationDTO> getReservations(Long restaurantId, LocalDate from, LocalDate to, 
                                               String status, String customerEmail, Pageable pageable) {
        List<ReservationDTO> filtered = reservations.values().stream()
                .filter(r -> restaurantId == null || r.getRestaurantId().equals(restaurantId))
                .filter(r -> from == null || !r.getReservationDate().isBefore(from))
                .filter(r -> to == null || !r.getReservationDate().isAfter(to))
                .filter(r -> status == null || status.equals(r.getStatus()))
                .filter(r -> customerEmail == null || customerEmail.equals(r.getCustomerEmail()))
                .sorted((r1, r2) -> r1.getReservationDate().compareTo(r2.getReservationDate()))
                .toList();

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    public ReservationDTO updateReservation(Long reservationId, ReservationDTO updates) {
        ReservationDTO reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        // Check availability for new time if changed
        if (!reservation.getReservationDate().equals(updates.getReservationDate()) ||
            !reservation.getReservationTime().equals(updates.getReservationTime())) {
            
            if (!checkAvailability(reservation.getRestaurantId(), 
                                 updates.getReservationDate(), 
                                 updates.getReservationTime())) {
                throw new IllegalStateException("New time slot not available.");
            }
        }

        // Update reservation fields
        reservation.setReservationDate(updates.getReservationDate());
        reservation.setReservationTime(updates.getReservationTime());
        reservation.setPartySize(updates.getPartySize());
        reservation.setSpecialRequests(updates.getSpecialRequests());
        reservation.setUpdatedAt(LocalDateTime.now());

        return reservation;
    }

    public void cancelReservation(Long reservationId, String reason) {
        ReservationDTO reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        reservation.setStatus("CANCELLED");
        reservation.setSpecialRequests(
            (reservation.getSpecialRequests() != null ? reservation.getSpecialRequests() + " | " : "") + 
            "Cancelled: " + reason
        );
        reservation.setUpdatedAt(LocalDateTime.now());

        // Send cancellation notification
        notificationService.sendReservationCancellation(
            reservation.getCustomerEmail(),
            "Restaurant Name" // Mock restaurant name
        );
    }

    public List<ReservationDTO> getReservationsByDate(Long restaurantId, LocalDate date) {
        return reservations.values().stream()
                .filter(r -> r.getRestaurantId().equals(restaurantId))
                .filter(r -> r.getReservationDate().equals(date))
                .sorted((r1, r2) -> r1.getReservationTime().compareTo(r2.getReservationTime()))
                .toList();
    }

    public ReservationDTO confirmReservation(Long reservationId) {
        ReservationDTO reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        reservation.setStatus("CONFIRMED");
        reservation.setUpdatedAt(LocalDateTime.now());
        return reservation;
    }

    public ReservationDTO cancelReservationStatus(Long reservationId) {
        ReservationDTO reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        reservation.setStatus("CANCELLED");
        reservation.setUpdatedAt(LocalDateTime.now());
        return reservation;
    }

    public ReservationDTO getReservationById(Long reservationId) {
        ReservationDTO reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }
        return reservation;
    }

    public List<ReservationDTO> getTodayReservations(Long restaurantId) {
        LocalDate today = LocalDate.now();
        return getReservationsByDate(restaurantId, today);
    }

    public List<ReservationDTO> getReservationsByCustomerEmail(String email) {
        return reservations.values().stream()
                .filter(r -> email.equals(r.getCustomerEmail()))
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .toList();
    }

    public List<ReservationDTO> getUpcomingReservations(Long restaurantId) {
        LocalDate today = LocalDate.now();
        return reservations.values().stream()
                .filter(r -> r.getRestaurantId().equals(restaurantId))
                .filter(r -> r.getReservationDate().isAfter(today) || 
                           (r.getReservationDate().equals(today) && 
                            r.getReservationTime().isAfter(LocalTime.now())))
                .filter(r -> r.isActive())
                .sorted((r1, r2) -> {
                    int dateComparison = r1.getReservationDate().compareTo(r2.getReservationDate());
                    if (dateComparison == 0) {
                        return r1.getReservationTime().compareTo(r2.getReservationTime());
                    }
                    return dateComparison;
                })
                .toList();
    }

    public List<ReservationDTO> getReservationsByStatus(Long restaurantId, String status) {
        return reservations.values().stream()
                .filter(r -> r.getRestaurantId().equals(restaurantId))
                .filter(r -> status.equals(r.getStatus()))
                .sorted((r1, r2) -> r1.getReservationDate().compareTo(r2.getReservationDate()))
                .toList();
    }

    public void markReservationSeated(Long reservationId) {
        ReservationDTO reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        reservation.setStatus("SEATED");
        reservation.setUpdatedAt(LocalDateTime.now());
    }

    public void markReservationCompleted(Long reservationId) {
        ReservationDTO reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        reservation.setStatus("COMPLETED");
        reservation.setUpdatedAt(LocalDateTime.now());
    }

    public void markReservationNoShow(Long reservationId) {
        ReservationDTO reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        reservation.setStatus("NO_SHOW");
        reservation.setUpdatedAt(LocalDateTime.now());
    }

    // Business helper methods
    private boolean isTimeConflict(LocalTime existingTime, LocalTime newTime) {
        // Consider 2-hour slots - check if times overlap
        LocalTime existingEnd = existingTime.plusHours(2);
        LocalTime newEnd = newTime.plusHours(2);
        
        return !(newTime.isAfter(existingEnd) || newEnd.isBefore(existingTime));
    }

    public boolean canCancelReservation(Long reservationId) {
        ReservationDTO reservation = reservations.get(reservationId);
        return reservation != null && 
               ("CONFIRMED".equals(reservation.getStatus()) || "SEATED".equals(reservation.getStatus()));
    }

    public boolean canModifyReservation(Long reservationId) {
        ReservationDTO reservation = reservations.get(reservationId);
        if (reservation == null) return false;
        
        // Can modify if status is CONFIRMED and reservation is in the future
        return "CONFIRMED".equals(reservation.getStatus()) && 
               (reservation.getReservationDate().isAfter(LocalDate.now()) ||
                (reservation.getReservationDate().equals(LocalDate.now()) && 
                 reservation.getReservationTime().isAfter(LocalTime.now().plusHours(1))));
    }

    // Analytics methods
    public int getTotalReservationsToday(Long restaurantId) {
        return getTodayReservations(restaurantId).size();
    }

    public int getNoShowCount(Long restaurantId, LocalDate date) {
        return (int) getReservationsByDate(restaurantId, date).stream()
                .filter(r -> "NO_SHOW".equals(r.getStatus()))
                .count();
    }

    public double getNoShowRate(Long restaurantId, LocalDate date) {
        List<ReservationDTO> dayReservations = getReservationsByDate(restaurantId, date);
        if (dayReservations.isEmpty()) return 0.0;
        
        long noShows = dayReservations.stream()
                .filter(r -> "NO_SHOW".equals(r.getStatus()))
                .count();
        
        return (double) noShows / dayReservations.size() * 100;
    }

    public List<LocalTime> getAvailableTimeSlots(Long restaurantId, LocalDate date) {
        // Business hours: 10 AM to 10 PM, every 30 minutes
        List<LocalTime> allSlots = generateTimeSlots();
        List<ReservationDTO> existingReservations = getReservationsByDate(restaurantId, date);
        
        return allSlots.stream()
                .filter(slot -> existingReservations.stream()
                        .noneMatch(r -> isTimeConflict(r.getReservationTime(), slot)))
                .toList();
    }

    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> slots = new java.util.ArrayList<>();
        LocalTime start = LocalTime.of(10, 0); // 10 AM
        LocalTime end = LocalTime.of(22, 0);   // 10 PM
        
        LocalTime current = start;
        while (current.isBefore(end)) {
            slots.add(current);
            current = current.plusMinutes(30);
        }
        
        return slots;
    }
}