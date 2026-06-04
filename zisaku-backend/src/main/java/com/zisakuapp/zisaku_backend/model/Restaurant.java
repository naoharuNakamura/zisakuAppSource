package com.zisakuapp.zisaku_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "m_restaurant")
@Data
public class Restaurant {
    @Id
    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "restaurant_name")   
    private String restaurantName;
    @Column(name = "restaurant_img")
    private String restaurantImg;
    @Column(name = "restaurant_rating")
    private Double restaurantRating;
    @Column(name = "restaurant_genre")
    private String restaurantGenre;
    @Column(name = "restaurant_price_range")
    private String restaurantPriceRange;
    @Column(name = "restaurant_area")
    private String restaurantArea;
    @Column(name = "restaurant_open_hour")
    private String restaurantOpenHour;
    @Column(name = "restaurant_close_hour")
    private String restaurantCloseHour;
    @Column(name = "restaurant_adress")
    private String restaurantAddress;
    @Column(name = "restaurant_phone")
    private String restaurantPhone;
    @Column(name = "restaurant_url")
    private String restaurantUrl;
    @Column(name = "restaurant_closed_days")
    private String restaurantClosedDays;

    @Transient
    private boolean isFavorite;

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}