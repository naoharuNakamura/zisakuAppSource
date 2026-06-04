package com.zisakuapp.zisaku_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zisakuapp.zisaku_backend.model.Restaurant;
import com.zisakuapp.zisaku_backend.model.UserRestaurant;
import com.zisakuapp.zisaku_backend.service.UserRestaurantService;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/t_user_restaurant")
public class UserRestaurantController {

    @Autowired
    private UserRestaurantService userRestaurantService;

    @GetMapping("/result")
    public List<UserRestaurant> getAllUserRestaurants() {
        return userRestaurantService.getAllUserRestaurants();
    }

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFavorite(@RequestBody Map<String, Object> payload) {
        Long userId = ((Number) payload.get("userId")).longValue();
        Long restaurantId = ((Number) payload.get("restaurantId")).longValue();

        var response = userRestaurantService.toggleFavorite(userId, restaurantId);
        return ResponseEntity.ok(response);
    }

    // ★ ("userId") を明記
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Long>> getUserFavorites(@PathVariable("userId") Long userId) {
        List<Long> restaurantIds = userRestaurantService.getUserFavorites(userId);
        return ResponseEntity.ok(restaurantIds);
    }

    // ★ ("userId") を明記
    @GetMapping("/user/{userId}/details")
    public ResponseEntity<List<Restaurant>> getFavoriteDetails(@PathVariable("userId") Long userId) {
        List<Restaurant> result = userRestaurantService.getFavoriteDetails(userId);
        return ResponseEntity.ok(result);
    }

    // ★ ("restaurantId") を明記
    @PostMapping("/result-detail/{restaurantId}")
    public ResponseEntity<?> editMemoRestaurant(
            @PathVariable("restaurantId") long restaurantId, 
            @RequestBody Map<String, Object> payload) {
        Long userId = ((Number) payload.get("userId")).longValue();
        String memo = (String) payload.get("memo");

        UserRestaurant updated = userRestaurantService.editMemoRestaurant(userId, restaurantId, memo);
        return ResponseEntity.ok(updated);
    }

    // ★ フロントの api.ts に合わせ、パスに userId と restaurantId の両方を含める形に修正
    // ★ 各 @PathVariable にも名前を明記
    @GetMapping("/user/{userId}/restaurant/{restaurantId}")
    public ResponseEntity<?> getMemoRestaurant(
            @PathVariable("userId") Long userId, 
            @PathVariable("restaurantId") Long restaurantId) {
            
        var opt = userRestaurantService.getUserRestaurant(userId, restaurantId);
        if (opt.isPresent()) {
            return ResponseEntity.ok(Map.of("memo", opt.get().getUserMemo()));
        } else {
            return ResponseEntity.ok(Map.of("memo", ""));
        }
    }
}