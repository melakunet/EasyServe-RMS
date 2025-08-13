
package com.easyserve.repository;

import com.easyserve.model.User;
import com.easyserve.model.User.Role;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    List<User> findByRestaurantIdAndIsActiveTrue(UUID restaurantId);

    List<User> findByRestaurantIdAndRole(UUID restaurantId, Role role);

    boolean existsByEmailAndRestaurantId(String email, UUID restaurantId);

    long countByRestaurantId(UUID restaurantId);

    @Query("SELECT u FROM User u WHERE u.restaurant.id = :restaurantId AND u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRoleAndRestaurant(@Param("restaurantId") UUID restaurantId,
                                                  @Param("role") Role role);
}
