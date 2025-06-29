package com.document.scan.model;


public class TTSResponse {
    public String status;
    public String message;
    public String audioUrl;
    public String error;

    public TTSResponse(String status, String message, String audioUrl, String error) {
        this.status = status;
        this.message = message;
        this.audioUrl = audioUrl;
        this.error = error;
    }

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}