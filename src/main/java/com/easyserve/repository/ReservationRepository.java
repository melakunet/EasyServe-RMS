
package com.easyserve.repository;

import com.easyserve.model.Reservation;
import com.easyserve.model.Reservation.Status;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByRestaurantIdAndReservationDateBetween(
            UUID restaurantId, LocalDate startDate, LocalDate endDate);

    List<Reservation> findByRestaurantIdAndReservationDate(
            UUID restaurantId, LocalDate date);

    List<Reservation> findByCustomerIdOrderByReservationDateDesc(UUID customerId);

    long countByRestaurantIdAndStatus(UUID restaurantId, Status status);

    List<Reservation> findByRestaurantIdAndStatusAndReservationDate(
            UUID restaurantId, Status status, LocalDate date);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.restaurant.id = :restaurantId AND r.status = 'NO_SHOW'")
    long countNoShows(@Param("restaurantId") UUID restaurantId);

    @Query("SELECT r FROM Reservation r WHERE r.restaurant.id = :restaurantId " +
           "AND r.status IN ('CONFIRMED', 'SEATED') AND r.reservationDate = :date")
    List<Reservation> findActiveReservationsForDate(@Param("restaurantId") UUID restaurantId,
                                                    @Param("date") LocalDate date);

    @Query("SELECT r FROM Reservation r WHERE LOWER(r.source) = LOWER(:source)")
    List<Reservation> findBySource(@Param("source") String source);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.restaurant.id = :restaurantId " +
           "AND r.status = 'CANCELLED'")
    long countCancelledReservations(@Param("restaurantId") UUID restaurantId);
}
