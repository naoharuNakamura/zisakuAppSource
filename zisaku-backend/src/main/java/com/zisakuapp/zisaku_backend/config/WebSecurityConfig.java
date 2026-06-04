package com.zisakuapp.zisaku_backend.config;

import com.zisakuapp.zisaku_backend.security.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS設定を有効化
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 🔑 【追加】内部フォワード（SpaRedirectController用）を許可する
                        // Spring Security 6以降では、コントローラーからの "forward:/index.html" 処理自体が
                        // セキュリティフィルターに引っかかる場合があるため、明示的に許可します。
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.FORWARD).permitAll()

                        // 🔑 トップページ、index.html、Vueのビルド生成物(assets)などの静的ファイルをすべて許可
                        .requestMatchers("/", "/index.html", "/assets/**", "/favicon.svg", "/icons.svg", "/routes.json")
                        .permitAll()

                        // ログイン・サインアップ関連のパスをすべて許可
                        .requestMatchers("/api/m_user/login/**", "/api/m_user/signup/**", "/api/signup/**",
                                "/api/login/**")
                        .permitAll()

                        // レストラン情報取得もすべて許可
                        .requestMatchers("/api/m_restaurant/**").permitAll()

                        // ✅ 【変更点1】 /api/ から始まる「その他のAPI」を認証必須にする
                        .requestMatchers("/api/**").authenticated()

                        // ✅ 【変更点2】 それ以外（Vueの画面URLなど）は一旦すべて許可し、SpaRedirectControllerへ流す
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Vueの開発サーバーからのアクセスを明示的に許可
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://zisakuapprication-e6a0ccazhbczb4ft.westcentralus-01.azurewebsites.net",
                "https://gray-desert-0ed283010.7.azurestaticapps.net",
                "https://zisakuapp-a7e4h4f9bqbyfhaj.canadacentral-01.azurewebsites.net"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
