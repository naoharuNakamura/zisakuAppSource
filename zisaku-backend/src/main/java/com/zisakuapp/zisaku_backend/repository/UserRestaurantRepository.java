package com.zisakuapp.zisaku_backend.repository;

import com.zisakuapp.zisaku_backend.model.UserRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

public interface UserRestaurantRepository extends JpaRepository<UserRestaurant, UserRestaurant.UserRestaurantId> {
    List<UserRestaurant> findByIdUserId(Long userId);
    
    Optional<UserRestaurant> findByIdUserIdAndIdRestaurantId(Long userId, Long restaurantId);
    
    @Transactional
    void deleteByIdUserIdAndIdRestaurantId(Long userId, Long restaurantId);
}