package com.zisakuapp.zisaku_backend.dto;

import com.zisakuapp.zisaku_backend.model.Restaurant;

public class RestaurantResponse {
    private Long restaurantId;
    private String restaurantName;
    private String restaurantGenre;
    private String restaurantArea;
    private String restaurantImg;
    private Double restaurantRating;
    private String restaurantPriceRange;
    private String restaurantOpenHour;
    private String restaurantCloseHour;
    private String restaurantClosedDays;
    private String restaurantPhone;
    private String restaurantAddress;
    private String restaurantUrl;

    public RestaurantResponse(Long restaurantId, String name, String genre, String area, String imageUrl, Double restaurantRating, String priceRange, String openHour, String closeHour, String closedDays, String phone, String address, String url
    ) {
        this.restaurantId = restaurantId;
        this.restaurantRating = restaurantRating;
        this.restaurantName = name;
        this.restaurantGenre = genre;
        this.restaurantArea = area;
        this.restaurantImg = imageUrl;
        this.restaurantPriceRange = priceRange;
        this.restaurantOpenHour = openHour;
        this.restaurantCloseHour = closeHour;
        this.restaurantClosedDays = closedDays;
        this.restaurantPhone = phone;
        this.restaurantAddress = address;
        this.restaurantUrl = url;
    }

 // 💡 追加：Restaurant エンティティから変換するためのコンストラクタ
    // ※ ゲッターの名前（getIdなど）は実際の Restaurant モデルの定義に合わせて調整してください
    public RestaurantResponse(Restaurant restaurant) {
        this.restaurantId = restaurant.getRestaurantId(); // もしモデル側が getId() なら変更してください
        this.restaurantName = restaurant.getRestaurantName();       // もしモデル側が getName() なら変更してください
        this.restaurantGenre = restaurant.getRestaurantGenre();
        this.restaurantArea = restaurant.getRestaurantArea();
        this.restaurantImg = restaurant.getRestaurantImg();
        this.restaurantRating = restaurant.getRestaurantRating();
        this.restaurantPriceRange = restaurant.getRestaurantPriceRange();
        this.restaurantOpenHour = restaurant.getRestaurantOpenHour();
        this.restaurantCloseHour = restaurant.getRestaurantCloseHour();
        this.restaurantClosedDays = restaurant.getRestaurantClosedDays();
        this.restaurantPhone = restaurant.getRestaurantPhone();
        this.restaurantAddress = restaurant.getRestaurantAddress();
        this.restaurantUrl = restaurant.getRestaurantUrl();
    }

    // ゲッター群
    public Long getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public String getRestaurantGenre() { return restaurantGenre; }
    public String getRestaurantArea() { return restaurantArea; }
    public String getRestaurantImg() { return restaurantImg; }
    public Double getRestaurantRating() { return restaurantRating; }
    public String getRestaurantPriceRange() { return restaurantPriceRange; }
    public String getRestaurantOpenHour() { return restaurantOpenHour; }
    public String getRestaurantCloseHour() { return restaurantCloseHour; }
    public String getRestaurantClosedDays() { return restaurantClosedDays; }
    public String getRestaurantPhone() { return restaurantPhone; }
    public String getRestaurantAddress() { return restaurantAddress; }
    public String getRestaurantUrl() { return restaurantUrl; }
}
