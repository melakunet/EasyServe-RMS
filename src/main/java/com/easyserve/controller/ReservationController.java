
package com.easyserve.controller;

import com.easyserve.dto.*;
import com.easyserve.model.Reservation;
import com.easyserve.model.Status;
import com.easyserve.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('STAFF')")
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody ReservationDTO dto) {
        ReservationDTO created = reservationService.createReservation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<ReservationDTO>> listReservations(
            @RequestParam Long restaurantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reservationDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ReservationDTO> result = reservationService.getReservations(
            restaurantId, from, to, status, customerEmail, pageable);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable Long id) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDTO dto) {
        ReservationDTO updated = reservationService.updateReservation(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        reservationService.cancelReservation(id, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @RequestParam Long restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam(defaultValue = "2") Integer partySize) {

        AvailabilityResponse availability = reservationService.checkAvailability(
            restaurantId, date, time, partySize);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/today")
    public ResponseEntity<List<ReservationDTO>> getTodayReservations(
            @RequestParam Long restaurantId) {
        List<ReservationDTO> reservations = reservationService.getTodayReservations(restaurantId);
        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable Long id) {
        ReservationDTO confirmed = reservationService.confirmReservation(id);
        return ResponseEntity.ok(confirmed);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationDTO> cancelReservationStatus(@PathVariable Long id) {
        ReservationDTO cancelled = reservationService.cancelReservationStatus(id);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<ReservationDTO>> getCustomerReservations(
            @PathVariable String email) {
        List<ReservationDTO> reservations = reservationService.getReservationsByCustomerEmail(email);
        return ResponseEntity.ok(reservations);
    }
}