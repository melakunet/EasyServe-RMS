
package com.easyserve.repository;

import java.util.UUID;

import com.easyserve.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID>{
    
    List<MenuItem> findByCategory(String category);
    
    List<MenuItem> findByAvailableTrue();
    
    List<MenuItem> findByAvailableFalse();
    
    List<MenuItem> findByNameContainingIgnoreCase(String name);
}
