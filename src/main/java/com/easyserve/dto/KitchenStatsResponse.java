

package com.easyserve.dto;


import java.time.Duration;

import java.time.Duration;

public class KitchenStatsResponse {
    private int activeOrders;
    private int pendingOrders;
    private int completedTodayOrders;
    private Duration averagePreparationTime;
    private double efficiency;
    private int totalActiveOrders;
private int ordersInPreparation;
private int ordersReady;
private int ordersCompleted;
private int totalOrdersToday;
    
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

    public int getTotalActiveOrders() {
    return totalActiveOrders;
}

public void setTotalActiveOrders(int totalActiveOrders) {
    this.totalActiveOrders = totalActiveOrders;
}

public int getOrdersInPreparation() {
    return ordersInPreparation;
}

public void setOrdersInPreparation(int ordersInPreparation) {
    this.ordersInPreparation = ordersInPreparation;
}

public int getOrdersReady() {
    return ordersReady;
}

public void setOrdersReady(int ordersReady) {
    this.ordersReady = ordersReady;
}

public int getOrdersCompleted() {
    return ordersCompleted;
}

public void setOrdersCompleted(int ordersCompleted) {
    this.ordersCompleted = ordersCompleted;
}

public int getTotalOrdersToday() {
    return totalOrdersToday;
}

public void setTotalOrdersToday(int totalOrdersToday) {
    this.totalOrdersToday = totalOrdersToday;
}
}
