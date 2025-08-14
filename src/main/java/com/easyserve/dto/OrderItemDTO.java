
package com.easyserve.dto;

import java.math.BigDecimal;

import java.util.UUID;

public class OrderItemDTO {
    private UUID menuItemId;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String customizations;
    private String notes;
    
    // Default constructor
    public OrderItemDTO() {}
    
    // Constructor with all fields
    public OrderItemDTO(UUID menuItemId, String itemName, Integer quantity, 
                       BigDecimal unitPrice, String customizations, String notes) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.customizations = customizations;
        this.notes = notes;
    }
    
    // Getters and Setters
    public UUID getMenuItemId() { 
        return menuItemId; 
    }
    
    public void setMenuItemId(UUID menuItemId) { 
        this.menuItemId = menuItemId; 
    }
    
    public String getItemName() { 
        return itemName; 
    }
    
    public void setItemName(String itemName) { 
        this.itemName = itemName; 
    }
    
    public Integer getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity; 
    }
    
    public BigDecimal getUnitPrice() { 
        return unitPrice; 
    }
    
    public void setUnitPrice(BigDecimal unitPrice) { 
        this.unitPrice = unitPrice; 
    }
    
    public String getCustomizations() { 
        return customizations; 
    }
    
    public void setCustomizations(String customizations) { 
        this.customizations = customizations; 
    }
    
    public String getNotes() { 
        return notes; 
    }
    
    public void setNotes(String notes) { 
        this.notes = notes; 
    }
    
    // Helper method to calculate total price for this item
    public BigDecimal getTotalPrice() {
        if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}