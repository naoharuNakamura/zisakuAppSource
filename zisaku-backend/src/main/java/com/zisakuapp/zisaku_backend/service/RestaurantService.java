package com.zisakuapp.zisaku_backend.service;

import com.zisakuapp.zisaku_backend.model.Restaurant;
import com.zisakuapp.zisaku_backend.repository.RestaurantRepository;
import com.zisakuapp.zisaku_backend.dto.RestaurantSearchRequest;
import com.zisakuapp.zisaku_backend.dto.RestaurantResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<String> getGenres() {
        return restaurantRepository.findDistinctGenres();
    }

    public List<String> getPriceRanges() {
        return restaurantRepository.findDistinctPriceRanges();
    }

    public List<String> getAreas() {
        return restaurantRepository.findDistinctAreas();
    }

    public List<Double> getRatings() {
        return restaurantRepository.findDistinctRatings();
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public RestaurantResponse getRestaurantDetail(Long id) {
        return restaurantRepository.findById(id)
                .map(RestaurantResponse::new)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
    }

    public Page<RestaurantResponse> searchRestaurants(RestaurantSearchRequest request, Pageable pageable) {
        Specification<Restaurant> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getRestaurantName() != null && !request.getRestaurantName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("restaurantName"),
                        "%" + request.getRestaurantName().toLowerCase() + "%"));
            }

            if (request.getRestaurantRating() != null && !request.getRestaurantRating().isEmpty()
                    && request.getRestaurantRating().contains("~")) {
                try {
                    String[] range = request.getRestaurantRating().split("~");
                    double min = Double.parseDouble(range[0]);
                    double max = Double.parseDouble(range[1]);
                    predicates.add(criteriaBuilder.between(root.get("restaurantRating"), min, max));
                } catch (NumberFormatException e) {
                    System.err.println("レーティングの数値変換エラー: " + request.getRestaurantRating());
                }
            }

            if (request.getRestaurantGenre() != null && !request.getRestaurantGenre().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("restaurantGenre"), request.getRestaurantGenre()));
            }

            if (request.getRestaurantPriceRange() != null && !request.getRestaurantPriceRange().isEmpty()) {
                String rawPrice = request.getRestaurantPriceRange();

                // 💡 数字以外の文字（\, ¥, ~, 〜, ?, カンマなど）をすべて半角スペースに置き換える
                // 例1: "\2,000?\4,000" -> " 2000 4000" -> トリムして "2000 4000"
                // 例2: "\6,000?" -> " 6000 " -> トリムして "6000"
                String cleaned = rawPrice.replaceAll("[^0-9]+", " ").trim();

                if (!cleaned.isEmpty()) {
                    String[] numbers = cleaned.split(" ");

                    if (numbers.length == 2) {
                        // 💡「¥1,000〜¥2,000」のパターンの時
                        // DBの文字列に "1000" と "2000" の両方が含まれているか（LIKE）で判定
                        predicates.add(criteriaBuilder.and(
                                criteriaBuilder.like(root.get("restaurantPriceRange"), "%" + numbers[0] + "%"),
                                criteriaBuilder.like(root.get("restaurantPriceRange"), "%" + numbers[1] + "%")));
                    } else if (numbers.length == 1) {
                        // 💡「¥6,000〜」のパターンの時
                        // DBの文字列に "6000" が含まれているか（LIKE）で判定
                        predicates.add(criteriaBuilder.like(root.get("restaurantPriceRange"), "%" + numbers[0] + "%"));
                    }
                }
            }

            if (request.getRestaurantArea() != null && !request.getRestaurantArea().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("restaurantArea"), request.getRestaurantArea()));
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            if (request.getIsAndSearch() != null && request.getIsAndSearch()) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            } else {
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            }
        };

        // 検索結果をエンティティから DTO(RestaurantResponse) に変換して返す
        return restaurantRepository.findAll(spec, pageable).map(RestaurantResponse::new);
    }
}