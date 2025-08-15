
package com.easyserve.repository;

import com.easyserve.model.Reservation;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.easyserve.model.Status;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRestaurantIdAndReservationDateBetween(
            Long restaurantId, LocalDate startDate, LocalDate endDate);

    List<Reservation> findByRestaurantIdAndReservationDate(
            Long restaurantId, LocalDate date);

    List<Reservation> findByCustomerIdOrderByReservationDateDesc(Long customerId);

    long countByRestaurantIdAndStatus(Long restaurantId, Status status);

    List<Reservation> findByRestaurantIdAndStatusAndReservationDate(
            Long restaurantId, Status status, LocalDate date);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.restaurant.id = :restaurantId AND r.status = 'NO_SHOW'")
    long countNoShows(@Param("restaurantId") Long restaurantId);

    @Query("SELECT r FROM Reservation r WHERE r.restaurant.id = :restaurantId " +
           "AND r.status IN ('CONFIRMED', 'SEATED') AND r.reservationDate = :date")
    List<Reservation> findActiveReservationsForDate(@Param("restaurantId") Long restaurantId,
                                                    @Param("date") LocalDate date);

    @Query("SELECT r FROM Reservation r WHERE LOWER(r.source) = LOWER(:source)")
    List<Reservation> findBySource(@Param("source") String source);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.restaurant.id = :restaurantId " +
           "AND r.status = 'CANCELLED'")
    long countCancelledReservations(@Param("restaurantId") Long restaurantId);
}
