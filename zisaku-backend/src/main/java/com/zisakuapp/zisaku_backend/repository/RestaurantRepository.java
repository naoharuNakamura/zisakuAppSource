package com.zisakuapp.zisaku_backend.repository;

import com.zisakuapp.zisaku_backend.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {

    // 🌟 すべてのフィールド名をキャメルケースに変更
    @Query("select distinct r.restaurantGenre from Restaurant r where r.restaurantGenre is not null and r.restaurantGenre != ''")
    List<String> findDistinctGenres();

    @Query("select distinct r.restaurantPriceRange from Restaurant r where r.restaurantPriceRange is not null and r.restaurantPriceRange != ''")
    List<String> findDistinctPriceRanges();

    @Query("select distinct r.restaurantArea from Restaurant r where r.restaurantArea is not null and r.restaurantArea != ''")
    List<String> findDistinctAreas();

    @Query("select distinct r.restaurantRating from Restaurant r where r.restaurantRating is not null")
    List<Double> findDistinctRatings();
}