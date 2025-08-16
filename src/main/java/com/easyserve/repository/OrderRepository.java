
package com.easyserve.repository;

import com.easyserve.model.Order;
import com.easyserve.model.Order.OrderType;
import com.easyserve.model.Order.Status;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByRestaurantIdAndStatus(Long restaurantId, Status status);

    List<Order> findByRestaurantIdAndCreatedAtBetween(Long restaurantId,
                                                       LocalDateTime start,
                                                       LocalDateTime end);

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Order> findByRestaurantIdAndOrderType(Long restaurantId, OrderType orderType);

    long countByRestaurantIdAndStatusAndCreatedAtBetween(Long restaurantId,
                                                          Status status,
                                                          LocalDateTime start,
                                                          LocalDateTime end);

    @Query("SELECT SUM(o.total) FROM Order o WHERE o.restaurant.id = :restaurantId " +
           "AND o.createdAt BETWEEN :start AND :end")
    BigDecimal calculateTotalSales(@Param("restaurantId") Long restaurantId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("SELECT AVG(o.total) FROM Order o WHERE o.restaurant.id = :restaurantId " +
           "AND o.createdAt BETWEEN :start AND :end")
    BigDecimal calculateAverageOrderValue(@Param("restaurantId") Long restaurantId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId " +
           "AND o.status IN ('NEW', 'CONFIRMED', 'PREPARING') " +
           "ORDER BY o.estimatedTime ASC")
    List<Order> findActiveKitchenOrders(@Param("restaurantId") Long restaurantId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId " +
           "AND o.status = 'CANCELLED'")
    long countCancelledOrders(@Param("restaurantId") Long restaurantId);
}
