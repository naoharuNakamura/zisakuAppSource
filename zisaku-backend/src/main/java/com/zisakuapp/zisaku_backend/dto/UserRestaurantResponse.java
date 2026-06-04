package com.zisakuapp.zisaku_backend.dto;

import lombok.Data;

@Data
public class UserRestaurantResponse {
    private String status; // "added" or "removed"

    public UserRestaurantResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
