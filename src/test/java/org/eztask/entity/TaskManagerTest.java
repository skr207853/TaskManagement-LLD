package org.eztask.entity;

import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.util.List;

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
        assertNotSame(tasks1, tasks2);
        assertEquals(tasks1.size(), tasks2.size());
    }

    @Test
    @DisplayName("Should not affect original list when modifying returned list")
    void testDefensiveCopyIsolation() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        manager.createTask("Task 1", "Description 1", creator);

        // Act
        List<Task> tasks = manager.getTaskList();
        int originalSize = tasks.size();
        Task newTask = new Task("Task 2", "Description 2", creator);
        tasks.add(newTask);

        List<Task> actualTasks = manager.getTaskList();

        // Assert
        assertEquals(originalSize, actualTasks.size());
        assertFalse(actualTasks.contains(newTask));
    }

    @Test
    @DisplayName("Should handle empty task list")
    void testEmptyTaskList() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();

        // Act
        List<Task> tasks = manager.getTaskList();

        // Assert
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    @DisplayName("Should update multiple task properties")
    void testUpdateMultipleTaskProperties() {
        // Arrange
        TaskManager manager = TaskManager.getInstance();
        manager.createTask("Task 1", "Description 1", creator);
        Task task = manager.getTaskList().get(0);

        // Act
        manager.assignTaskToUser(task, assignee);
        manager.updateTaskStatus(task, TaskStatus.DEV_IN_PROGRESS);
        manager.updateTaskPriority(task, TaskPriority.HIGH);

        // Assert
        assertEquals(assignee, task.getAssignee());
        assertEquals(TaskStatus.DEV_IN_PROGRESS, task.getTaskStatus());
        assertEquals(TaskPriority.HIGH, task.getTaskPriority());
    }
}
