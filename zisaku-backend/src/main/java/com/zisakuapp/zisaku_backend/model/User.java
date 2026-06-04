package com.zisakuapp.zisaku_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "m_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "user_password", nullable = false)
    private String userPassword;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    @Column(name = "user_phone", nullable = false)
    private String userPhone;
}
