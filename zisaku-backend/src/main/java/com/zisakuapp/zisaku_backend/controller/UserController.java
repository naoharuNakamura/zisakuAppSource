package com.zisakuapp.zisaku_backend.controller;

import com.zisakuapp.zisaku_backend.model.User;
import com.zisakuapp.zisaku_backend.service.*;
import com.zisakuapp.zisaku_backend.dto.UserLoginRequest;
import com.zisakuapp.zisaku_backend.dto.UserResponse;
import com.zisakuapp.zisaku_backend.dto.LoginResponse; // 💡 追加
import com.zisakuapp.zisaku_backend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager; // 💡 追加

@RestController
@RequestMapping("/api/m_user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager; // 💡 追加

    // ユーザー作成（サインアップ用）
    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody User user) {

        // 💡 データベースに保存する直前に、最終チェックを行う
        if (userService.existsByUserEmail(user.getUserEmail())) {
            // 重複していたら、500エラーではなく「409 Conflict」として返す
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        // 重複していなければ通常通り保存する
        UserResponse createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // ログイン

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest) {
        try {
            // 💡 1. 認証処理を Spring Security に丸投げする！
            // パスワードの暗号化検証なども自動でやってくれます
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserEmail(),
                            loginRequest.getUserPassword()));

            // 💡 2. 認証が通ったら、DBからユーザー情報を取ってくる
            // (UserService の authenticateUser メソッドはもう使わないので、findById等で取得)
            UserResponse user = userService.getUserByUserEmail(loginRequest.getUserEmail());

            // 💡 3. トークン生成して返す (既存と同じ)
            String token = jwtUtil.generateToken(user.getUserEmail());
            return ResponseEntity.ok(new LoginResponse(token, user));

        } catch (org.springframework.security.core.AuthenticationException e) {
            // Spring Security が認証失敗と判断した場合はここに来る
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    // プロフィール更新
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser) {
        try {
            System.out.println(updatedUser);
            // ロジックはServiceにお任せ
            User savedUser = userService.updateProfile(updatedUser);
            UserResponse userResponse = new UserResponse(savedUser);
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            // ユーザーが見つからなかった場合など
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String userEmail) {
        // サービス層で存在チェックを行い、結果（boolean）を返す
        boolean exists = userService.existsByUserEmail(userEmail);
        return ResponseEntity.ok(exists);
    }
}