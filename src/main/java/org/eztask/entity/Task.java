package org.eztask.entity;

import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private String desc;
    private User assignee;
    private TaskStatus taskStatus;
    private TaskPriority taskPriority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User creater;
    private List<Comment> comments;

    public Task(String title, String desc, User creater) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.desc = desc;
        this.creater = creater;
        this.createdAt = LocalDateTime.now();
        this.comments = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getAssignee() {
        return this.assignee;
    }

    public TaskPriority getTaskPriority() {
        return this.taskPriority;
    }

    public TaskStatus getTaskStatus() {
        return this.taskStatus;
    }

    public User getCreater() {
        return this.creater;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", assignee=" + assignee +
                ", taskStatus=" + taskStatus +
                ", taskPriority=" + taskPriority +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", creater=" + creater +
                ", comments=" + comments.toString() +
                '}';
    }
}
