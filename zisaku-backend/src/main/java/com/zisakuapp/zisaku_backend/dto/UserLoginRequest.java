package com.zisakuapp.zisaku_backend.dto;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String userEmail;
    private String userPassword;

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
