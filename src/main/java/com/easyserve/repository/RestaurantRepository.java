
package com.easyserve.repository;

import com.easyserve.model.Restaurant;
import com.easyserve.model.Restaurant.SubscriptionPlan;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByEmail(String email);

    List<Restaurant> findByIsActiveTrue();

    List<Restaurant> findBySubscriptionPlan(SubscriptionPlan plan);

    boolean existsByEmail(String email);

    Optional<Restaurant> findByIdAndIsActiveTrue(Long id);

    @Query("SELECT COUNT(r) FROM Restaurant r WHERE r.isActive = true")
    long countActiveRestaurants();

    @Query("SELECT r FROM Restaurant r WHERE r.createdAt BETWEEN :start AND :end")
    List<Restaurant> findRestaurantsCreatedBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Restaurant> searchByNameOrEmail(@Param("query") String query);
}
