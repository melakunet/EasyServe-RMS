

package com.easyserve.dto;


import java.time.Duration;

import java.time.Duration;

public class KitchenStatsResponse {
    private int activeOrders;
    private int pendingOrders;
    private int completedTodayOrders;
    private Duration averagePreparationTime;
    private double efficiency;
    
    // Default constructor
    public KitchenStatsResponse() {}
    
    // Constructor with all fields
    public KitchenStatsResponse(int activeOrders, int pendingOrders, int completedTodayOrders, 
                               Duration averagePreparationTime, double efficiency) {
        this.activeOrders = activeOrders;
        this.pendingOrders = pendingOrders;
        this.completedTodayOrders = completedTodayOrders;
        this.averagePreparationTime = averagePreparationTime;
        this.efficiency = efficiency;
    }
    
    // Getters and Setters
    public int getActiveOrders() { 
        return activeOrders; 
    }
    
    public void setActiveOrders(int activeOrders) { 
        this.activeOrders = activeOrders; 
    }
    
    public int getPendingOrders() { 
        return pendingOrders; 
    }
    
    public void setPendingOrders(int pendingOrders) { 
        this.pendingOrders = pendingOrders; 
    }
    
    public int getCompletedTodayOrders() { 
        return completedTodayOrders; 
    }
    
    public void setCompletedTodayOrders(int completedTodayOrders) { 
        this.completedTodayOrders = completedTodayOrders; 
    }
    
    public Duration getAveragePreparationTime() { 
        return averagePreparationTime; 
    }
    
    public void setAveragePreparationTime(Duration averagePreparationTime) { 
        this.averagePreparationTime = averagePreparationTime; 
    }
    
    public double getEfficiency() { 
        return efficiency; 
    }
    
    public void setEfficiency(double efficiency) { 
        this.efficiency = efficiency; 
    }
}
