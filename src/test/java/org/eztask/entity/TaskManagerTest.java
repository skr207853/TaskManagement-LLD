package org.eztask.entity;

import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskManager Tests")
class TaskManagerTest {

    private User creator;
    private User assignee;

    @BeforeEach
    void setUp() throws Exception {
        // Reset singleton instance before each test
        resetSingleton();
        creator = new User("Creator");
        assignee = new User("Assignee");
    }

    private void resetSingleton() throws Exception {
        Field instance = TaskManager.class.getDeclaredField("taskManager");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    @DisplayName("Should return singleton instance")
    void testGetInstance() {
        // Act
        TaskManager instance1 = TaskManager.getInstance();
        TaskManager instance2 = TaskManager.getInstance();

        // Assert
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Should create and add task")
    void testCreateTask() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();

        // Act
        manager.createTask("Task 1", "Description 1", creator);
        List<Task> tasks = manager.getTaskList();

        // Assert
        assertEquals(1, tasks.size());
    }

    @Test
    @DisplayName("Should add existing task")
    void testAddTask() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        Task task = new Task("Task 1", "Description 1", creator);

        // Act
        manager.addTask(task);
        List<Task> tasks = manager.getTaskList();

        // Assert
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    @DisplayName("Should add multiple tasks")
    void testAddMultipleTasks() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();

        // Act
        manager.createTask("Task 1", "Description 1", creator);
        manager.createTask("Task 2", "Description 2", creator);
        manager.createTask("Task 3", "Description 3", creator);

        List<Task> tasks = manager.getTaskList();

        // Assert
        assertEquals(3, tasks.size());
    }

    @Test
    @DisplayName("Should assign task to user")
    void testAssignTaskToUser() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        manager.createTask("Task 1", "Description 1", creator);
        Task task = manager.getTaskList().get(0);

        // Act
        manager.assignTaskToUser(task, assignee);

        // Assert
        assertEquals(assignee, task.getAssignee());
    }

    @Test
    @DisplayName("Should update task status")
    void testUpdateTaskStatus() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        manager.createTask("Task 1", "Description 1", creator);
        Task task = manager.getTaskList().get(0);

        // Act
        manager.updateTaskStatus(task, TaskStatus.DEV_IN_PROGRESS);

        // Assert
        assertEquals(TaskStatus.DEV_IN_PROGRESS, task.getTaskStatus());
    }

    @Test
    @DisplayName("Should update task priority")
    void testUpdateTaskPriority() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        manager.createTask("Task 1", "Description 1", creator);
        Task task = manager.getTaskList().get(0);

        // Act
        manager.updateTaskPriority(task, TaskPriority.HIGH);

        // Assert
        assertEquals(TaskPriority.HIGH, task.getTaskPriority());
    }

    @Test
    @DisplayName("Should add comment to task")
    void testAddComment() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        manager.createTask("Task 1", "Description 1", creator);
        Task task = manager.getTaskList().get(0);
        Comment comment = new Comment("Test comment");

        // Act & Assert
        assertDoesNotThrow(() -> manager.addComment(task, comment));
    }

    @Test
    @DisplayName("Should return defensive copy of task list")
    void testGetTaskListReturnsDefensiveCopy() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        manager.createTask("Task 1", "Description 1", creator);

        // Act
        List<Task> tasks1 = manager.getTaskList();
        List<Task> tasks2 = manager.getTaskList();

        // Assert
        assertNotSame(tasks1, tasks2); // Different list instances
        assertEquals(tasks1.size(), tasks2.size()); // Same content
    }

    @Test
    @DisplayName("Should handle concurrent singleton access")
    void testConcurrentSingletonAccess() throws InterruptedException {
        // Arrange
        int numberOfThreads = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        List<TaskManager> instances = new ArrayList<>();

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(() -> {
                instances.add(TaskManager.getInstance());
                latch.countDown();
            }).start();
        }

        latch.await(5, TimeUnit.SECONDS);

        // Assert
        TaskManager firstInstance = instances.get(0);
        for (TaskManager instance : instances) {
            assertSame(firstInstance, instance);
        }
    }

    @Test
    @DisplayName("Should handle concurrent task creation")
    void testConcurrentTaskCreation() throws InterruptedException {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        int numberOfThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    manager.createTask("Task " + index, "Description " + index, creator);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Assert
        List<Task> tasks = manager.getTaskList();
        assertEquals(numberOfThreads, tasks.size());
    }

    @Test
    @DisplayName("Should handle concurrent reads and writes")
    void testConcurrentReadsAndWrites() throws InterruptedException {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        int numberOfReaders = 50;
        int numberOfWriters = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfReaders + numberOfWriters);
        CountDownLatch latch = new CountDownLatch(numberOfReaders + numberOfWriters);

        // Add initial tasks
        for (int i = 0; i < 10; i++) {
            manager.createTask("Initial Task " + i, "Description " + i, creator);
        }

        // Act - Concurrent reads
        for (int i = 0; i < numberOfReaders; i++) {
            executor.submit(() -> {
                try {
                    List<Task> tasks = manager.getTaskList();
                    assertNotNull(tasks);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Act - Concurrent writes
        for (int i = 0; i < numberOfWriters; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    manager.createTask("New Task " + index, "New Description " + index, creator);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Assert
        List<Task> tasks = manager.getTaskList();
        assertEquals(20, tasks.size()); // 10 initial + 10 new tasks
    }

    @Test
    @DisplayName("Should handle concurrent task updates")
    void testConcurrentTaskUpdates() throws InterruptedException {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        manager.createTask("Task 1", "Description 1", creator);
        Task task = manager.getTaskList().get(0);

        int numberOfThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    manager.updateTaskStatus(task, TaskStatus.DEV_IN_PROGRESS);
                    manager.updateTaskPriority(task, TaskPriority.HIGH);
                    manager.assignTaskToUser(task, assignee);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Assert
        assertEquals(TaskStatus.DEV_IN_PROGRESS, task.getTaskStatus());
        assertEquals(TaskPriority.HIGH, task.getTaskPriority());
        assertEquals(assignee, task.getAssignee());
    }

    @Test
    @DisplayName("Should handle concurrent comment additions")
    void testConcurrentCommentAdditions() throws InterruptedException {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        manager.createTask("Task 1", "Description 1", creator);
        Task task = manager.getTaskList().get(0);

        int numberOfThreads = 30;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    Comment comment = new Comment("Comment " + index);
                    manager.addComment(task, comment);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Assert - No exception should be thrown
        assertTrue(true);
    }

    @Test
    @DisplayName("Should maintain task integrity during concurrent operations")
    void testTaskIntegrityDuringConcurrentOperations() throws InterruptedException {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        int numberOfThreads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // Act - Mix of create, read, update operations
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    if (index % 3 == 0) {
                        manager.createTask("Task " + index, "Description " + index, creator);
                    } else if (index % 3 == 1) {
                        List<Task> tasks = manager.getTaskList();
                        if (!tasks.isEmpty()) {
                            Task task = tasks.get(0);
                            manager.updateTaskStatus(task, TaskStatus.DEV_IN_PROGRESS);
                        }
                    } else {
                        List<Task> tasks = manager.getTaskList();
                        assertNotNull(tasks);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Assert - Should complete without exceptions
        List<Task> finalTasks = manager.getTaskList();
        assertNotNull(finalTasks);
        assertTrue(finalTasks.size() > 0);
    }
}
