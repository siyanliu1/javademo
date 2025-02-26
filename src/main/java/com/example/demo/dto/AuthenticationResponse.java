package com.example.demo.dto;

public class AuthenticationResponse {
    private String message;
    private String accessToken;

    public AuthenticationResponse() {}
    public AuthenticationResponse(String message, String accessToken) {
        this.message = message;
        this.accessToken = accessToken;
    }

    // Getters and Setters

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
