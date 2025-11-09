package org.eztask.entity;

import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task Entity Tests")
class TaskTest {

    private User creator;
    private User assignee;

    @BeforeEach
    void setUp() {
        creator = new User("Creator");
        assignee = new User("Assignee");
    }

    @Test
    @DisplayName("Should create task with valid parameters")
    void testTaskCreation() {
        // Arrange & Act
        Task task = new Task("Test Task", "Test Description", creator);

        // Assert
        assertNotNull(task);
        assertNotNull(task.getCreater());
        assertEquals("Creator", task.getCreater().getName());
        assertNull(task.getAssignee());
        assertNull(task.getTaskStatus());
        assertNull(task.getTaskPriority());
    }

    @Test
    @DisplayName("Should set and get assignee")
    void testSetAssignee() {
        // Arrange
        Task task = new Task("Task", "Description", creator);

        // Act
        task.setAssignee(assignee);

        // Assert
        assertNotNull(task.getAssignee());
        assertEquals("Assignee", task.getAssignee().getName());
    }

    @Test
    @DisplayName("Should set and get task status")
    void testSetTaskStatus() {
        // Arrange
        Task task = new Task("Task", "Description", creator);

        // Act
        task.setTaskStatus(TaskStatus.DEV_IN_PROGRESS);

        // Assert
        assertEquals(TaskStatus.DEV_IN_PROGRESS, task.getTaskStatus());
    }

    @Test
    @DisplayName("Should set and get task priority")
    void testSetTaskPriority() {
        // Arrange
        Task task = new Task("Task", "Description", creator);

        // Act
        task.setTaskPriority(TaskPriority.HIGH);

        // Assert
        assertEquals(TaskPriority.HIGH, task.getTaskPriority());
    }

    @Test
    @DisplayName("Should set and get updated time")
    void testSetUpdatedAt() {
        // Arrange
        Task task = new Task("Task", "Description", creator);
        LocalDateTime updateTime = LocalDateTime.now();

        // Act
        task.setUpdatedAt(updateTime);

        // Assert
        assertDoesNotThrow(() -> task.setUpdatedAt(updateTime));
    }

    @Test
    @DisplayName("Should add comment to task")
    void testAddComment() {
        // Arrange
        Task task = new Task("Task", "Description", creator);
        Comment comment = new Comment("First comment");

        // Act & Assert
        assertDoesNotThrow(() -> task.addComment(comment));
    }

    @Test
    @DisplayName("Should add multiple comments to task")
    void testAddMultipleComments() {
        // Arrange
        Task task = new Task("Task", "Description", creator);
        Comment comment1 = new Comment("First comment");
        Comment comment2 = new Comment("Second comment");

        // Act
        task.addComment(comment1);
        task.addComment(comment2);

        // Assert
        assertDoesNotThrow(() -> {
            task.addComment(comment1);
            task.addComment(comment2);
        });
    }

    @Test
    @DisplayName("Should handle null assignee")
    void testNullAssignee() {
        // Arrange
        Task task = new Task("Task", "Description", creator);

        // Act
        task.setAssignee(null);

        // Assert
        assertNull(task.getAssignee());
    }

    @Test
    @DisplayName("Should handle null status")
    void testNullStatus() {
        // Arrange
        Task task = new Task("Task", "Description", creator);

        // Act
        task.setTaskStatus(null);

        // Assert
        assertNull(task.getTaskStatus());
    }

    @Test
    @DisplayName("Should handle null priority")
    void testNullPriority() {
        // Arrange
        Task task = new Task("Task", "Description", creator);

        // Act
        task.setTaskPriority(null);

        // Assert
        assertNull(task.getTaskPriority());
    }

    @Test
    @DisplayName("Should update task state")
    void testTaskStateUpdate() {
        // Arrange
        Task task = new Task("Task", "Description", creator);

        // Act
        task.setAssignee(assignee);
        task.setTaskStatus(TaskStatus.NOT_PICKED);
        task.setTaskPriority(TaskPriority.MODERATE);
        LocalDateTime updateTime = LocalDateTime.now();
        task.setUpdatedAt(updateTime);

        // Assert
        assertEquals(assignee, task.getAssignee());
        assertEquals(TaskStatus.NOT_PICKED, task.getTaskStatus());
        assertEquals(TaskPriority.MODERATE, task.getTaskPriority());
    }

    @Test
    @DisplayName("Should have string representation")
    void testToString() {
        // Arrange
        Task task = new Task("Task", "Description", creator);
        task.setAssignee(assignee);
        task.setTaskStatus(TaskStatus.DEV_IN_PROGRESS);

        // Act
        String result = task.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Task"));
        assertTrue(result.contains("Description"));
    }
}
