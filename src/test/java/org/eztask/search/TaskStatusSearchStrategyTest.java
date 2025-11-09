package org.eztask.search;

import org.eztask.entity.Task;
import org.eztask.entity.User;
import org.eztask.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskStatusSearchStrategy Tests")
class TaskStatusSearchStrategyTest {

    private TaskStatusSearchStrategy strategy;
    private User creator;
    private Task task;

    @BeforeEach
    void setUp() {
        strategy = new TaskStatusSearchStrategy();
        creator = new User("Creator");
        task = new Task("Test Task", "Description", creator);
    }

    @Test
    @DisplayName("Should match task with same status")
    void testMatchesWithSameStatus() {
        // Arrange
        task.setTaskStatus(TaskStatus.DEV_IN_PROGRESS);

        // Act
        boolean result = strategy.matches(task, TaskStatus.DEV_IN_PROGRESS);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should not match task with different status")
    void testDoesNotMatchWithDifferentStatus() {
        // Arrange
        task.setTaskStatus(TaskStatus.DEV_IN_PROGRESS);

        // Act
        boolean result = strategy.matches(task, TaskStatus.NOT_PICKED);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should not match task with no status")
    void testDoesNotMatchTaskWithNoStatus() {
        // Arrange - task has no status

        // Act
        boolean result = strategy.matches(task, TaskStatus.DEV_IN_PROGRESS);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when criteria is not TaskStatus")
    void testReturnsFalseWhenCriteriaIsNotTaskStatus() {
        // Arrange
        task.setTaskStatus(TaskStatus.DEV_IN_PROGRESS);

        // Act
        boolean result = strategy.matches(task, "Not a TaskStatus");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when criteria is null")
    void testReturnsFalseWhenCriteriaIsNull() {
        // Arrange
        task.setTaskStatus(TaskStatus.DEV_IN_PROGRESS);

        // Act
        boolean result = strategy.matches(task, null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should match all TaskStatus values")
    void testMatchesAllTaskStatusValues() {
        // Test all enum values
        for (TaskStatus status : TaskStatus.values()) {
            // Arrange
            task.setTaskStatus(status);

            // Act
            boolean result = strategy.matches(task, status);

            // Assert
            assertTrue(result, "Should match for status: " + status);
        }
    }

    @Test
    @DisplayName("Should handle task status set to null")
    void testHandlesTaskStatusNull() {
        // Arrange
        task.setTaskStatus(null);

        // Act
        boolean result = strategy.matches(task, TaskStatus.DEV_IN_PROGRESS);

        // Assert
        assertFalse(result);
    }
}
