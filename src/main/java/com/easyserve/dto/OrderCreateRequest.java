

package com.easyserve.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OrderCreateRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Table number is required")
    private Integer tableNumber;

    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "At least one item must be ordered")
    private List<Long> menuItemIds;

    public OrderCreateRequest() {
    }

    public OrderCreateRequest(Long customerId, Integer tableNumber, List<Long> menuItemIds) {
        this.customerId = customerId;
        this.tableNumber = tableNumber;
        this.menuItemIds = menuItemIds;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<Long> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(List<Long> menuItemIds) {
        this.menuItemIds = menuItemIds;
    }
}

