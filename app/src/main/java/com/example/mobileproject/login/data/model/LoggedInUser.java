package com.example.mobileproject.login.data.model;

import android.util.Log;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String token;

    public LoggedInUser(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() {
        Log.d("fake", "getUserId: "+userId);
        return userId;
    }

    public String getToken() { return token; }
}
