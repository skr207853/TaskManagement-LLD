package org.eztask.entity;

import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TaskManager {
    private final List<Task> taskList;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private static volatile TaskManager taskManager = null;

    public static TaskManager getInstance() {
        if (taskManager == null) {
            synchronized (TaskManager.class) {
                if (taskManager == null) {
                    taskManager = new TaskManager();
                }
            }
        }
        return taskManager;
    }

    private TaskManager() {
        taskList = new ArrayList<>();
    }

    public void addTask(Task task) {
        lock.writeLock().lock();
        try {
            taskList.add(task);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void createTask(String title, String desc, User creater) {
        Task task = new Task(title, desc, creater);
        lock.writeLock().lock();
        try {
            taskList.add(task);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addComment(Task task, Comment comment) {
        synchronized (task) {
            task.setUpdatedAt(comment.getCreationTime());
            task.addComment(comment);
        }
    }

    public void assignTaskToUser(Task task, User user) {
        synchronized (task) {
            task.setUpdatedAt(LocalDateTime.now());
            task.setAssignee(user);
        }
    }

    public void updateTaskStatus(Task task, TaskStatus status) {
        synchronized (task) {
            task.setUpdatedAt(LocalDateTime.now());
            task.setTaskStatus(status);
        }
    }

    public void updateTaskPriority(Task task, TaskPriority priority) {
        synchronized (task) {
            task.setUpdatedAt(LocalDateTime.now());
            task.setTaskPriority(priority);
        }
    }
    public List<Task> getTaskList() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(taskList);
        } finally {
            lock.readLock().unlock();
        }
    }
}
