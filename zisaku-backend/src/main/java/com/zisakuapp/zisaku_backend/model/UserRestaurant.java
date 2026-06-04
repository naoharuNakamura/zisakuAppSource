package com.zisakuapp.zisaku_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRestaurant {

    private UserRestaurantId id;
    private Boolean isFavorite;
    private String userMemo;

    @Data
    public static class UserRestaurantId implements Serializable {
        private int userId;
        private int restaurantId;

        public UserRestaurantId() {}
        public UserRestaurantId(int userId, int restaurantId) {
            this.userId = userId;
            this.restaurantId = restaurantId;
        }
    }
}