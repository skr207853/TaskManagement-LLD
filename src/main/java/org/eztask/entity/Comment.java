package org.eztask.entity;

import java.time.LocalDateTime;

public class Comment {
    private final String id;
    private final String text;
    private final LocalDateTime creationTime;

    public Comment(String text) {
        this.id = java.util.UUID.randomUUID().toString();
        this.text = text;
        this.creationTime = LocalDateTime.now();
    }


    public LocalDateTime getCreationTime() {
        return this.creationTime;
    }
}
