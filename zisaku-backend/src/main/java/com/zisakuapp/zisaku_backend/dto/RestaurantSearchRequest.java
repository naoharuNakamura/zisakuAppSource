package com.zisakuapp.zisaku_backend.dto;

import lombok.Data;

@Data
public class RestaurantSearchRequest {
    private String restaurantName;
    private String restaurantRating;      // 修正：restaurant_rating → restaurantRating
    private String restaurantGenre;       // 修正：restaurant_genre → restaurantGenre
    private String restaurantPriceRange;  // 修正：restaurant_price_range → restaurantPriceRange
    private String restaurantArea;        // 修正：restaurant_area → restaurantArea
    private Boolean isAndSearch;

    // Getter と Setter
    public String getRestaurantRating() {
        return restaurantRating;
    }

    public String getRestaurantGenre() {
        return restaurantGenre;
    }

    public String getRestaurantPriceRange() {
        return restaurantPriceRange;
    }

    public String getRestaurantArea() {
        return restaurantArea;
    }

    public void setRestaurantRating(String restaurantRating) {
        this.restaurantRating = restaurantRating;
    }

    public void setRestaurantGenre(String restaurantGenre) {
        this.restaurantGenre = restaurantGenre;
    }

    public void setRestaurantPriceRange(String restaurantPriceRange) {
        this.restaurantPriceRange = restaurantPriceRange;
    }

    public void setRestaurantArea(String restaurantArea) {
        this.restaurantArea = restaurantArea;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Boolean getIsAndSearch() {
        return isAndSearch;
    }

    public void setIsAndSearch(Boolean isAndSearch) {
        this.isAndSearch = isAndSearch;
    }
}
