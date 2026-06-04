package com.zisakuapp.zisaku_backend.dto;

import lombok.Data;

@Data
public class UserRestaurantRequest {
    private Long userId;
    private Long restaurantId;

    public Long getUserId() {
        return userId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
