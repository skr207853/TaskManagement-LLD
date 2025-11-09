package org.eztask;

import org.eztask.entity.Comment;
import org.eztask.entity.Task;
import org.eztask.entity.User;
import org.eztask.entity.TaskManager;
import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;
import org.eztask.search.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        TaskManager taskManager = TaskManager.getInstance();

        User alice = new User("Alice");
        User bob = new User("Bob");
        User charlie = new User("Charlie");

        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Add tasks concurrently
        for (int i = 1; i <= 5; i++) {
            int taskNum = i;
            executor.submit(() -> {
                taskManager.createTask("Task " + taskNum, "Description for task " + taskNum, alice);
            });
        }

        // Slight delay to ensure tasks are created
        Thread.sleep(1000);

        // Concurrently assign users and add comments
        List<Task> tasks = taskManager.getTaskList();

        for (Task task : tasks) {
            executor.submit(() -> {
                taskManager.assignTaskToUser(task, bob);
                taskManager.updateTaskStatus(task, TaskStatus.DEV_IN_PROGRESS);
                taskManager.updateTaskPriority(task, TaskPriority.MODERATE);

                Comment comment = new Comment("Concurrent comment");
                taskManager.addComment(task, comment);
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(500);
        }

        // Print all tasks after modification
        System.out.println("Final task states after concurrent modifications:");
        taskManager.getTaskList().forEach(System.out::println);


    }
}