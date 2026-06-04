package com.zisakuapp.zisaku_backend.controller;

import com.zisakuapp.zisaku_backend.model.Restaurant;
import com.zisakuapp.zisaku_backend.service.RestaurantService;
import com.zisakuapp.zisaku_backend.dto.RestaurantSearchRequest;
import com.zisakuapp.zisaku_backend.dto.RestaurantResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

@RestController
@RequestMapping("/api/m_restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/genres")
    public List<String> getGenres() {
        return restaurantService.getGenres();
    }

    @GetMapping("/price-ranges")
    public List<String> getPriceRanges() {
        return restaurantService.getPriceRanges();
    }

    @GetMapping("/areas")
    public List<String> getAreas() {
        return restaurantService.getAreas();
    }

    @GetMapping("/ratings")
    public List<Double> getRatings() {
        return restaurantService.getRatings();
    }

    @GetMapping("/result")
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantDetail(@PathVariable(name = "id") Long id) {
        System.out.println("★詳細リクエスト受信 ID: " + id);
        return ResponseEntity.ok(restaurantService.getRestaurantDetail(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RestaurantResponse>> searchRestaurants(
            @ModelAttribute RestaurantSearchRequest request,
            // 💡 引数を Pageable に変更し、デフォルト値をアノテーションで指定
            @PageableDefault(page = 0, size = 50) Pageable pageable) {

        System.out.println("★検索リクエスト受信: " + request);
        System.out.println("★適用されたソート・ページング: " + pageable);

        // 💡 手動での PageRequest.of(page, size) の組み立ては不要になります！
        Page<RestaurantResponse> results = restaurantService.searchRestaurants(request, pageable);
        return ResponseEntity.ok(results);
    }
}