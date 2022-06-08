package com.example.smsreader.models;

public class ReportMessage {

    private String message;

    private String isoCode;

    private Boolean isPhishing;

    public ReportMessage(String message, String isoCode, Boolean isPhishing) {
        this.message = message;
        this.isoCode = isoCode;
        this.isPhishing = isPhishing;
    }
}
