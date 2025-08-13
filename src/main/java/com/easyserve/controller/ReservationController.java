
package com.easyserve.controller;

import com.easyserve.dto.*;
import com.easyserve.model.Reservation;
import com.easyserve.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('STAFF')")
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody ReservationDTO dto) {
        Reservation reservation = reservationService.createReservation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReservationDTO.from(reservation));
    }

    @GetMapping
    public ResponseEntity<Page<ReservationDTO>> listReservations(
            @RequestParam UUID restaurantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) ReservationDTO.Status status,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reservationDate,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.by(sort[0]).with(Order.Direction.fromString(sort[1]))));
        // For simplicity assume service can handle filtering—add that later
        Page<ReservationDTO> result = reservationService.getReservationsByDate(restaurantId, from)
                .stream()
                .map(ReservationDTO::from)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        list -> new PageImpl<>(list, pageable, list.size())));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable UUID id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(ReservationDTO.from(reservation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable UUID id,
            @Valid @RequestBody ReservationDTO dto) {
        Reservation updated = reservationService.updateReservation(id, dto);
        return ResponseEntity.ok(ReservationDTO.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        reservationService.cancelReservation(id, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @RequestParam UUID restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {

        boolean available = reservationService.checkAvailability(restaurantId, date, time);
        return ResponseEntity.ok(new AvailabilityResponse(date, time, available));
    }

    @GetMapping("/today")
    public ResponseEntity<List<ReservationDTO>> getTodayReservations(
            @RequestParam UUID restaurantId) {
        List<ReservationDTO> list = reservationService
                .getReservationsByDate(restaurantId, LocalDate.now())
                .stream()
                .map(ReservationDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable UUID id) {
        Reservation confirmed = reservationService.confirmReservation(id);
        return ResponseEntity.ok(ReservationDTO.from(confirmed));
    }
}
