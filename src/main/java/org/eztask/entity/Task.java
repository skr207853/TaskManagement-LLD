package org.eztask.entity;

import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Task {
    private final String id;
    private final String title;
    private final String desc;
    private User assignee;
    private TaskStatus taskStatus;
    private TaskPriority taskPriority;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final User creater;
    private final List<Comment> comments;

    public Task(String title, String desc, User creater) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.desc = desc;
        this.creater = creater;
        this.createdAt = LocalDateTime.now();
        this.comments = new CopyOnWriteArrayList<>();
    }

    public synchronized void addComment(Comment comment) {
        comments.add(comment);
    }

    public synchronized void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public synchronized void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public synchronized void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public synchronized void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public synchronized User getAssignee() {
        return this.assignee;
    }

    public synchronized TaskPriority getTaskPriority() {
        return this.taskPriority;
    }

    public synchronized TaskStatus getTaskStatus() {
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
