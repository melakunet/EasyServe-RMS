
package com.easyserve.repository;

import com.easyserve.model.User;
import com.easyserve.model.User.Role;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    List<User> findByRestaurantIdAndIsActiveTrue(Long restaurantId);

    List<User> findByRestaurantIdAndRole(Long restaurantId, Role role);

    boolean existsByEmailAndRestaurantId(String email, Long restaurantId);

    long countByRestaurantId(Long restaurantId);

    @Query("SELECT u FROM User u WHERE u.restaurant.id = :restaurantId AND u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRoleAndRestaurant(@Param("restaurantId") Long restaurantId,
                                                  @Param("role") Role role);
}