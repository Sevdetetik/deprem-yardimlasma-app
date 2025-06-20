package com.example.bitrimeproje.model;

public class Request {
    private String requestId;
    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String description;
    private long timestamp;

    public Request() {
        // Boş constructor Firestore için gerekli
    }

    public Request(String requestId, String userId, String username, String email, String phoneNumber, String description, long timestamp) {
        this.requestId = requestId;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Getter ve Setter'lar...
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
