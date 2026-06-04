package com.zisakuapp.zisaku_backend.service;

import com.zisakuapp.zisaku_backend.model.User;
import com.zisakuapp.zisaku_backend.repository.UserRepository;
import com.zisakuapp.zisaku_backend.dto.UserResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return new UserResponse(user);
    }

    public UserResponse getUserByUserEmail(String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        return new UserResponse(user);
    }

    public boolean existsByUserEmail(String userEmail) {
        return userRepository.existsByUserEmail(userEmail);
    }

    public UserResponse createUser(User user) {
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }

// 💡 安全なデータ詰め替えロジックに修正
    public User updateProfile(User updatedUser) {
        System.out.println(updatedUser);
        // 1. 既存のユーザーをIDで検索
        User existingUser = userRepository.findByUserId(updatedUser.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + updatedUser.getUserId()));
        System.out.println("This is"+existingUser);
        
        // 2. フロントから送られてきた値で上書き（null/空は無視して既存値を保持）
        if (updatedUser.getUserName() != null && !updatedUser.getUserName().isEmpty()) {
            existingUser.setUserName(updatedUser.getUserName());
        }
        if (updatedUser.getUserEmail() != null && !updatedUser.getUserEmail().isEmpty()) {
            existingUser.setUserEmail(updatedUser.getUserEmail());
        }
        if (updatedUser.getUserPhone() != null && !updatedUser.getUserPhone().isEmpty()) {
            existingUser.setUserPhone(updatedUser.getUserPhone());
        }
        
        // 3. パスワードが入力されている場合のみ上書き（空なら既存のパスワードを維持）
        if (updatedUser.getUserPassword() != null && !updatedUser.getUserPassword().isEmpty()) {
            existingUser.setUserPassword(passwordEncoder.encode(updatedUser.getUserPassword()));
        }
        
        // 4. 保存して返す
        return userRepository.save(existingUser);
    }

}
