package com.example.smsreader.models;

public class CallAPI {
    private String message;

    public CallAPI(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
