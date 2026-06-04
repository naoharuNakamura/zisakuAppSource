package com.zisakuapp.zisaku_backend.service;

import com.zisakuapp.zisaku_backend.model.Restaurant;
import com.zisakuapp.zisaku_backend.model.UserRestaurant;
import com.zisakuapp.zisaku_backend.repository.RestaurantRepository;
import com.zisakuapp.zisaku_backend.repository.UserRestaurantRepository;
import com.zisakuapp.zisaku_backend.dto.UserRestaurantResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRestaurantService {

    @Autowired
    private UserRestaurantRepository userRestaurantRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<UserRestaurant> getAllUserRestaurants() {
        return userRestaurantRepository.findAll();
    }

    public UserRestaurantResponse toggleFavorite(Long userId, Long restaurantId) {
        Optional<UserRestaurant> existing = userRestaurantRepository.findByIdUserIdAndIdRestaurantId(userId, restaurantId);

        if (existing.isPresent()) {
            userRestaurantRepository.deleteByIdUserIdAndIdRestaurantId(userId, restaurantId);
            return new UserRestaurantResponse("removed");
        } else {
            UserRestaurant userRestaurant = new UserRestaurant();
            UserRestaurant.UserRestaurantId id = new UserRestaurant.UserRestaurantId(userId, restaurantId);
            userRestaurant.setId(id);
            userRestaurant.setIsFavorite(true);

            userRestaurantRepository.save(userRestaurant);
            return new UserRestaurantResponse("added");
        }
    }

    public UserRestaurant getMemoRestaurant(Long userId, Long restaurantId) {
        Optional<UserRestaurant> existing = userRestaurantRepository.findByIdUserIdAndIdRestaurantId(userId, restaurantId);
        return existing.orElse(null);
    }

    public UserRestaurant editMemoRestaurant(Long userId, Long restaurantId, String memo){
        Optional<UserRestaurant> existing = userRestaurantRepository.findByIdUserIdAndIdRestaurantId(userId, restaurantId);

        UserRestaurant userRestaurant;
        if (existing.isPresent()) {
            userRestaurant = existing.get();
            userRestaurant.setUserMemo(memo);
        } else {
            userRestaurant = new UserRestaurant();
            UserRestaurant.UserRestaurantId id = new UserRestaurant.UserRestaurantId(userId, restaurantId);
            userRestaurant.setId(id);
            userRestaurant.setUserMemo(memo);
            userRestaurant.setIsFavorite(false);
        }

        return userRestaurantRepository.save(userRestaurant);
    }

    public List<Long> getUserFavorites(Long userId) {
        List<UserRestaurant> favorites = userRestaurantRepository.findByIdUserId(userId);
        return favorites.stream()
                .filter(fav -> Boolean.TRUE.equals(fav.getIsFavorite()))
                .map(fav -> fav.getId().getRestaurantId())
                .toList();
    }
    
    public List<Restaurant> getFavoriteDetails(Long userId) {
        List<UserRestaurant> favorites = userRestaurantRepository.findByIdUserId(userId);

        List<Long> ids = favorites.stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsFavorite()))
                .map(r -> r.getId().getRestaurantId())
                .toList();

        if (ids.isEmpty()) {
            return List.of();
        }

        return restaurantRepository.findAllById(ids);
    }

    public Optional<UserRestaurant> getUserRestaurant(Long userId, Long restaurantId) {
        return userRestaurantRepository.findByIdUserIdAndIdRestaurantId(userId, restaurantId);
    }
}