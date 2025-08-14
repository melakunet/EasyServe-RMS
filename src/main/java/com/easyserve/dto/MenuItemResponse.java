

package com.easyserve.dto;
import java.math.BigDecimal;


import java.util.UUID;

public class MenuItemResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private boolean available;
    private String imageUrl;
    private Integer preparationTime; // in minutes
    
    // Default constructor
    public MenuItemResponse() {}
    
    // Constructor with all fields
    public MenuItemResponse(UUID id, String name, String description, BigDecimal price, 
                           String category, boolean available, String imageUrl, Integer preparationTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
        this.imageUrl = imageUrl;
        this.preparationTime = preparationTime;
    }
    
    // Getters and Setters
    public UUID getId() { 
        return id; 
    }
    
    public void setId(UUID id) { 
        this.id = id; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public BigDecimal getPrice() { 
        return price; 
    }
    
    public void setPrice(BigDecimal price) { 
        this.price = price; 
    }
    
    public String getCategory() { 
        return category; 
    }
    
    public void setCategory(String category) { 
        this.category = category; 
    }
    
    public boolean isAvailable() { 
        return available; 
    }
    
    public void setAvailable(boolean available) { 
        this.available = available; 
    }
    
    public String getImageUrl() { 
        return imageUrl; 
    }
    
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; 
    }
    
    public Integer getPreparationTime() { 
        return preparationTime; 
    }
    
    public void setPreparationTime(Integer preparationTime) { 
        this.preparationTime = preparationTime; 
    }
}
