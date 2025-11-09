package org.eztask.integration;

import org.eztask.entity.*;
import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;
import org.eztask.search.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Multithreaded Integration Tests")
class MultithreadedIntegrationTest {

    private TaskManager taskManager;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();
        taskManager = TaskManager.getInstance();
        executorService = Executors.newFixedThreadPool(20);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private void resetSingleton() throws Exception {
        Field instance = TaskManager.class.getDeclaredField("taskManager");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    @DisplayName("Should handle concurrent task creation from multiple threads")
    void testConcurrentTaskCreation() throws InterruptedException {
        // Arrange
        int numberOfThreads = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        User creator = new User("Creator");

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            final int taskNumber = i;
            executorService.submit(() -> {
                try {
                    taskManager.createTask("Task " + taskNumber, "Description " + taskNumber, creator);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        // Assert
        List<Task> tasks = taskManager.getTaskList();
        assertEquals(numberOfThreads, tasks.size(), "All tasks should be created");
    }

    @Test
    @DisplayName("Should handle concurrent reads and writes without data corruption")
    void testConcurrentReadsAndWrites() throws InterruptedException {
        // Arrange
        User creator = new User("Creator");
        int numberOfReaders = 50;
        int numberOfWriters = 50;
        CountDownLatch latch = new CountDownLatch(numberOfReaders + numberOfWriters);

        // Create initial tasks
        for (int i = 0; i < 10; i++) {
            taskManager.createTask("Task " + i, "Description " + i, creator);
        }

        // Act - Concurrent readers
        for (int i = 0; i < numberOfReaders; i++) {
            executorService.submit(() -> {
                try {
                    List<Task> tasks = taskManager.getTaskList();
                    assertNotNull(tasks);
                    assertTrue(tasks.size() >= 10);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Act - Concurrent writers
        for (int i = 0; i < numberOfWriters; i++) {
            final int taskNumber = i;
            executorService.submit(() -> {
                try {
                    taskManager.createTask("New Task " + taskNumber, "New Description " + taskNumber, creator);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(15, TimeUnit.SECONDS);

        // Assert
        List<Task> finalTasks = taskManager.getTaskList();
        assertEquals(60, finalTasks.size(), "Should have 10 initial + 50 new tasks");
    }

    @Test
    @DisplayName("Should handle concurrent task updates without race conditions")
    void testConcurrentTaskUpdates() throws InterruptedException {
        // Arrange
        User creator = new User("Creator");
        User assignee = new User("Assignee");
        taskManager.createTask("Test Task", "Description", creator);
        Task task = taskManager.getTaskList().get(0);

        int numberOfThreads = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // Act - Multiple threads updating the same task
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    taskManager.assignTaskToUser(task, assignee);
                    taskManager.updateTaskStatus(task, TaskStatus.DEV_IN_PROGRESS);
                    taskManager.updateTaskPriority(task, TaskPriority.HIGH);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        // Assert - Final state should be consistent
        assertEquals(assignee, task.getAssignee());
        assertEquals(TaskStatus.DEV_IN_PROGRESS, task.getTaskStatus());
        assertEquals(TaskPriority.HIGH, task.getTaskPriority());
    }

    @Test
    @DisplayName("Should handle concurrent comment additions without data loss")
    void testConcurrentCommentAdditions() throws InterruptedException {
        // Arrange
        User creator = new User("Creator");
        taskManager.createTask("Task", "Description", creator);
        Task task = taskManager.getTaskList().get(0);

        int numberOfComments = 100;
        CountDownLatch latch = new CountDownLatch(numberOfComments);

        // Act
        for (int i = 0; i < numberOfComments; i++) {
            final int commentNumber = i;
            executorService.submit(() -> {
                try {
                    Comment comment = new Comment("Comment " + commentNumber);
                    taskManager.addComment(task, comment);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        // Assert
        List<Comment> comments = task.getComments();
        assertEquals(numberOfComments, comments.size(), "All comments should be added");
    }

    @Test
    @DisplayName("Should handle concurrent searches while tasks are being modified")
    void testConcurrentSearchesWithModifications() throws InterruptedException {
        // Arrange
        User creator1 = new User("Creator1");
        User creator2 = new User("Creator2");
        User assignee = new User("Assignee");

        // Create initial tasks
        for (int i = 0; i < 20; i++) {
            User creator = (i % 2 == 0) ? creator1 : creator2;
            taskManager.createTask("Task " + i, "Description " + i, creator);
        }

        int numberOfOperations = 100;
        CountDownLatch latch = new CountDownLatch(numberOfOperations);
        AtomicInteger searchSuccesses = new AtomicInteger(0);

        // Act - Mix of searches and modifications
        for (int i = 0; i < numberOfOperations; i++) {
            final int operation = i % 4;
            executorService.submit(() -> {
                try {
                    if (operation == 0) {
                        // Search by creator
                        TaskSearcher searcher = new TaskSearcher(new TaskCreaterSearchStrategy());
                        List<Task> results = searcher.search(taskManager.getTaskList(), creator1);
                        assertNotNull(results);
                        searchSuccesses.incrementAndGet();
                    } else if (operation == 1) {
                        // Update task status
                        List<Task> tasks = taskManager.getTaskList();
                        if (!tasks.isEmpty()) {
                            Task task = tasks.get(0);
                            taskManager.updateTaskStatus(task, TaskStatus.DEV_IN_PROGRESS);
                        }
                    } else if (operation == 2) {
                        // Search by status
                        TaskSearcher searcher = new TaskSearcher(new TaskStatusSearchStrategy());
                        List<Task> results = searcher.search(taskManager.getTaskList(), TaskStatus.DEV_IN_PROGRESS);
                        assertNotNull(results);
                        searchSuccesses.incrementAndGet();
                    } else {
                        // Assign tasks
                        List<Task> tasks = taskManager.getTaskList();
                        if (!tasks.isEmpty()) {
                            Task task = tasks.get(tasks.size() - 1);
                            taskManager.assignTaskToUser(task, assignee);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(15, TimeUnit.SECONDS);

        // Assert
        assertTrue(searchSuccesses.get() > 0, "Searches should complete successfully");
        assertEquals(20, taskManager.getTaskList().size(), "Task count should remain consistent");
    }

    @Test
    @DisplayName("Should maintain data integrity in complex concurrent workflow")
    void testComplexConcurrentWorkflow() throws InterruptedException {
        // Arrange
        User creator1 = new User("Creator1");
        User creator2 = new User("Creator2");
        User assignee1 = new User("Assignee1");
        User assignee2 = new User("Assignee2");

        int numberOfOperations = 200;
        CountDownLatch latch = new CountDownLatch(numberOfOperations);
        AtomicInteger tasksCreated = new AtomicInteger(0);
        AtomicInteger tasksAssigned = new AtomicInteger(0);
        AtomicInteger tasksUpdated = new AtomicInteger(0);

        // Act - Complex mix of operations
        for (int i = 0; i < numberOfOperations; i++) {
            final int operationType = i % 5;
            final int taskNumber = i;

            executorService.submit(() -> {
                try {
                    switch (operationType) {
                        case 0: // Create task
                            User creator = (taskNumber % 2 == 0) ? creator1 : creator2;
                            taskManager.createTask("Task " + taskNumber, "Desc " + taskNumber, creator);
                            tasksCreated.incrementAndGet();
                            break;

                        case 1: // Assign task
                            List<Task> tasks = taskManager.getTaskList();
                            if (!tasks.isEmpty()) {
                                Task task = tasks.get(0);
                                User assignee = (taskNumber % 2 == 0) ? assignee1 : assignee2;
                                taskManager.assignTaskToUser(task, assignee);
                                tasksAssigned.incrementAndGet();
                            }
                            break;

                        case 2: // Update status
                            tasks = taskManager.getTaskList();
                            if (!tasks.isEmpty()) {
                                Task task = tasks.get(tasks.size() - 1);
                                TaskStatus status = (taskNumber % 2 == 0) ? 
                                    TaskStatus.DEV_IN_PROGRESS : TaskStatus.NOT_PICKED;
                                taskManager.updateTaskStatus(task, status);
                                tasksUpdated.incrementAndGet();
                            }
                            break;

                        case 3: // Update priority
                            tasks = taskManager.getTaskList();
                            if (!tasks.isEmpty()) {
                                Task task = tasks.get(tasks.size() / 2);
                                TaskPriority priority = (taskNumber % 2 == 0) ? 
                                    TaskPriority.HIGH : TaskPriority.LOW;
                                taskManager.updateTaskPriority(task, priority);
                            }
                            break;

                        case 4: // Add comment
                            tasks = taskManager.getTaskList();
                            if (!tasks.isEmpty()) {
                                Task task = tasks.get(0);
                                Comment comment = new Comment("Comment " + taskNumber);
                                taskManager.addComment(task, comment);
                            }
                            break;
                    }
                } catch (Exception e) {
                    fail("Exception during concurrent operation: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(20, TimeUnit.SECONDS);

        // Assert
        List<Task> finalTasks = taskManager.getTaskList();
        assertNotNull(finalTasks);
        assertTrue(tasksCreated.get() > 0, "Tasks should be created");
        assertTrue(finalTasks.size() > 0, "Task list should not be empty");
        
        System.out.println("Tasks created: " + tasksCreated.get());
        System.out.println("Tasks assigned: " + tasksAssigned.get());
        System.out.println("Tasks updated: " + tasksUpdated.get());
        System.out.println("Final task count: " + finalTasks.size());
    }

    @Test
    @DisplayName("Should handle concurrent singleton access correctly")
    void testConcurrentSingletonAccess() throws InterruptedException {
        // Arrange
        int numberOfThreads = 200;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        List<TaskManager> instances = new CopyOnWriteArrayList<>();

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    TaskManager instance = TaskManager.getInstance();
                    instances.add(instance);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        // Assert
        assertEquals(numberOfThreads, instances.size());
        TaskManager firstInstance = instances.get(0);
        for (TaskManager instance : instances) {
            assertSame(firstInstance, instance, "All instances should be the same");
        }
    }

    @Test
    @DisplayName("Should handle stress test with mixed operations")
    void testStressTestMixedOperations() throws InterruptedException {
        // Arrange
        User[] users = new User[10];
        for (int i = 0; i < 10; i++) {
            users[i] = new User("User" + i);
        }

        int numberOfThreads = 500;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger errors = new AtomicInteger(0);

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    User randomUser = users[index % users.length];
                    
                    if (index % 3 == 0) {
                        taskManager.createTask("Stress Task " + index, "Description " + index, randomUser);
                    } else if (index % 3 == 1) {
                        List<Task> tasks = taskManager.getTaskList();
                        if (!tasks.isEmpty()) {
                            Task task = tasks.get(0);
                            taskManager.updateTaskStatus(task, TaskStatus.DEV_IN_PROGRESS);
                            taskManager.updateTaskPriority(task, TaskPriority.MODERATE);
                        }
                    } else {
                        TaskSearcher searcher = new TaskSearcher(new TaskCreaterSearchStrategy());
                        List<Task> results = searcher.search(taskManager.getTaskList(), randomUser);
                        assertNotNull(results);
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);

        // Assert
        assertEquals(0, errors.get(), "No errors should occur during stress test");
        List<Task> finalTasks = taskManager.getTaskList();
        assertNotNull(finalTasks);
        assertTrue(finalTasks.size() > 0, "Tasks should be created");
    }

    @Test
    @DisplayName("Should verify thread-safety of all search strategies concurrently")
    void testConcurrentSearchStrategies() throws InterruptedException {
        // Arrange
        User creator = new User("Creator");
        User assignee = new User("Assignee");

        for (int i = 0; i < 50; i++) {
            taskManager.createTask("Task " + i, "Description " + i, creator);
        }

        // Set various properties
        List<Task> tasks = taskManager.getTaskList();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (i % 2 == 0) {
                taskManager.assignTaskToUser(task, assignee);
                taskManager.updateTaskStatus(task, TaskStatus.DEV_IN_PROGRESS);
                taskManager.updateTaskPriority(task, TaskPriority.HIGH);
            }
        }

        int numberOfSearches = 200;
        CountDownLatch latch = new CountDownLatch(numberOfSearches);
        AtomicInteger successfulSearches = new AtomicInteger(0);

        // Act - Concurrent searches with different strategies
        for (int i = 0; i < numberOfSearches; i++) {
            final int searchType = i % 4;
            executorService.submit(() -> {
                try {
                    TaskSearcher searcher;
                    List<Task> results;

                    switch (searchType) {
                        case 0:
                            searcher = new TaskSearcher(new TaskCreaterSearchStrategy());
                            results = searcher.search(taskManager.getTaskList(), creator);
                            break;
                        case 1:
                            searcher = new TaskSearcher(new TaskAssigneeSearchStrategy());
                            results = searcher.search(taskManager.getTaskList(), assignee);
                            break;
                        case 2:
                            searcher = new TaskSearcher(new TaskStatusSearchStrategy());
                            results = searcher.search(taskManager.getTaskList(), TaskStatus.DEV_IN_PROGRESS);
                            break;
                        default:
                            searcher = new TaskSearcher(new TaskPrioritySearchStrategy());
                            results = searcher.search(taskManager.getTaskList(), TaskPriority.HIGH);
                            break;
                    }

                    assertNotNull(results);
                    successfulSearches.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(15, TimeUnit.SECONDS);

        // Assert
        assertEquals(numberOfSearches, successfulSearches.get(), "All searches should complete successfully");
    }
}
