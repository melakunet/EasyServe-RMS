

package com.easyserve.service;

import com.easyserve.model.MenuItem;
import com.easyserve.repository.MenuItemRepository;
import com.easyserve.dto.MenuItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    public MenuItem saveMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category);
    }

    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByAvailableTrue();
    }

   public List<MenuItemResponse> getMenuForRestaurant(Long restaurantId) {
    // TODO: Filter by restaurant when Restaurant relationship is added
    return menuItemRepository.findByAvailableTrue().stream()
            .map(menuItem -> {
                MenuItemResponse response = new MenuItemResponse();
                response.setId(menuItem.getId());
                response.setName(menuItem.getName());
                response.setDescription(menuItem.getDescription());
                response.setPrice(menuItem.getPrice());
                response.setCategory(menuItem.getCategory());
                response.setAvailable(menuItem.getAvailable());
                return response;
            })
            .toList();
}
}
