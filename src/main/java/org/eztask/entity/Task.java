package org.eztask.entity;

import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Task {
    private final String id;
    private final String title;
    private final String desc;
    private final LocalDateTime createdAt;
    private final User creater;
    private final List<Comment> comments;
    private User assignee;
    private TaskStatus taskStatus;
    private TaskPriority taskPriority;
    private LocalDateTime updatedAt;

    private final Object assigneeLock = new Object();
    private final Object taskStatusLock = new Object();
    private final Object taskPriorityLock = new Object();
    private final Object updatedAtLock = new Object();


    public Task(String title, String desc, User creater) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.desc = desc;
        this.creater = creater;
        this.createdAt = LocalDateTime.now();
        this.comments = new CopyOnWriteArrayList<>();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public List<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    public void setAssignee(User assignee) {
        synchronized (assigneeLock) {
            this.assignee = assignee;
        }
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        synchronized (taskStatusLock) {
            this.taskStatus = taskStatus;
        }
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        synchronized (taskPriorityLock) {
            this.taskPriority = taskPriority;
        }
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        synchronized (updatedAtLock) {
            this.updatedAt = updatedAt;
        }
    }

    public User getAssignee() {
        synchronized (assigneeLock) {
            return this.assignee;
        }
    }

    public TaskPriority getTaskPriority() {
        synchronized (taskStatusLock) {
            return this.taskPriority;
        }
    }

    public TaskStatus getTaskStatus() {
        synchronized (taskPriorityLock) {
            return this.taskStatus;
        }
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
