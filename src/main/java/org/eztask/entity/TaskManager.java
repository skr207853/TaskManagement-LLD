package org.eztask.entity;

import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> taskList;

    private static volatile TaskManager taskManager = null;

    private static TaskManager getInstance() {
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
        taskList.add(task);
    }

    public void createTask(String title, String desc, User creater) {
        Task task = new Task(title, desc, creater);
        taskList.add(task);
    }

    public void addComment(Task task, Comment comment) {
        task.setUpdatedAt(comment.getCreationTime());
        task.addComment(comment);
    }

    public void assignTaskToUser(Task task, User user) {
        task.setUpdatedAt(LocalDateTime.now());
        task.setAssignee(user);
    }

    public void updateTaskStatus(Task task, TaskStatus status) {
        task.setUpdatedAt(LocalDateTime.now());
        task.setTaskStatus(status);
    }

    public void updateTaskPriority(Task task, TaskPriority priority) {
        task.setUpdatedAt(LocalDateTime.now());
        task.setTaskPriority(priority);
    }
    public List<Task> getTaskList() {
        return new ArrayList<>(taskList);
    }
}
