package org.eztask.entity;

import java.time.LocalDateTime;

public class Comment {
    private String id;
    private String text;
    private LocalDateTime creationTime;

    public Comment(String text) {
        this.text = text;
        this.creationTime = LocalDateTime.now();
    }


    public LocalDateTime getCreationTime() {
        return this.creationTime;
    }
}
