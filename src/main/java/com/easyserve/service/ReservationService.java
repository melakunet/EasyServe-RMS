
package com.easyserve.service;

import com.easyserve.dto.ReservationDTO;
import com.easyserve.model.*;
import com.easyserve.repository.CustomerRepository;
import com.easyserve.repository.ReservationRepository;
import com.easyserve.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;

    public Reservation createReservation(ReservationDTO dto) {
        if (!checkAvailability(dto.getRestaurantId(), dto.getReservationDate(), dto.getReservationTime())) {
            throw new IllegalStateException("Time slot not available.");
        }

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Reservation reservation = Reservation.builder()
                .restaurant(restaurant)
                .customer(customer)
                .reservationDate(dto.getReservationDate())
                .reservationTime(dto.getReservationTime())
                .partySize(dto.getPartySize())
                .status(Reservation.Status.CONFIRMED)
                .specialRequests(dto.getSpecialRequests())
                .source(dto.getSource())
                .tableNumber(dto.getTableNumber())
                .build();

        Reservation saved = reservationRepository.save(reservation);
        notificationService.sendReservationConfirmation(saved);
        return saved;
    }

    public boolean checkAvailability(UUID restaurantId, LocalDate date, LocalTime time) {
        List<Reservation> existing = reservationRepository.findActiveReservationsForDate(restaurantId, date);
        return existing.stream().noneMatch(r -> r.getReservationTime().equals(time));
    }

    public Reservation updateReservation(UUID reservationId, ReservationDTO updates) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        reservation.setReservationDate(updates.getReservationDate());
        reservation.setReservationTime(updates.getReservationTime());
        reservation.setPartySize(updates.getPartySize());
        reservation.setSpecialRequests(updates.getSpecialRequests());
        reservation.setTableNumber(updates.getTableNumber());

        return reservationRepository.save(reservation);
    }

    public void cancelReservation(UUID reservationId, String reason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        reservation.setStatus(Reservation.Status.CANCELLED);
        reservation.setSpecialRequests((reservation.getSpecialRequests() != null ? reservation.getSpecialRequests() + " | " : "") + "Cancelled: " + reason);
        reservationRepository.save(reservation);

        notificationService.sendReservationCancellation(reservation);
    }

    public List<Reservation> getReservationsByDate(UUID restaurantId, LocalDate date) {
        return reservationRepository.findByRestaurantIdAndReservationDate(restaurantId, date);
    }

    public Reservation confirmReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        reservation.setStatus(Reservation.Status.CONFIRMED);
        return reservationRepository.save(reservation);
    }
}
