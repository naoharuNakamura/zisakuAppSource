package com.zisakuapp.zisaku_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "t_user_restaurant")
@Data
public class UserRestaurant {

    @EmbeddedId
    private UserRestaurantId id;

    @Column(name = "is_favorite")
    private Boolean isFavorite;

    @Column(name = "user_memo")
    private String userMemo;

    @Embeddable
    @Data
    public static class UserRestaurantId implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "restaurant_id")
        private Long restaurantId;

        public UserRestaurantId() {}
        public UserRestaurantId(Long userId, Long restaurantId) {
            this.userId = userId;
            this.restaurantId = restaurantId;
        }
    }
}