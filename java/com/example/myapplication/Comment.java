package com.example.myapplication;

public class Comment {
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 2;

    private int type;
    private String mediaUrl;
    private long timestamp;

    public Comment(int type, String mediaUrl) {
        this.type = type;
        this.mediaUrl = mediaUrl;
        this.timestamp = System.currentTimeMillis();
    }

    public int getType() {
        return type;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }
} 