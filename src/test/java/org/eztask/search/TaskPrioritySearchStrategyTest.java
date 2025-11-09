package org.eztask.search;

import org.eztask.entity.Task;
import org.eztask.entity.User;
import org.eztask.enums.TaskPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskPrioritySearchStrategy Tests")
class TaskPrioritySearchStrategyTest {

    private TaskPrioritySearchStrategy strategy;
    private User creator;
    private Task task;

    @BeforeEach
    void setUp() {
        strategy = new TaskPrioritySearchStrategy();
        creator = new User("Creator");
        task = new Task("Test Task", "Description", creator);
    }

    @Test
    @DisplayName("Should match task with same priority")
    void testMatchesWithSamePriority() {
        // Arrange
        task.setTaskPriority(TaskPriority.HIGH);

        // Act
        boolean result = strategy.matches(task, TaskPriority.HIGH);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should not match task with different priority")
    void testDoesNotMatchWithDifferentPriority() {
        // Arrange
        task.setTaskPriority(TaskPriority.HIGH);

        // Act
        boolean result = strategy.matches(task, TaskPriority.LOW);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should not match task with no priority")
    void testDoesNotMatchTaskWithNoPriority() {
        // Arrange - task has no priority

        // Act
        boolean result = strategy.matches(task, TaskPriority.HIGH);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when criteria is not TaskPriority")
    void testReturnsFalseWhenCriteriaIsNotTaskPriority() {
        // Arrange
        task.setTaskPriority(TaskPriority.HIGH);

        // Act
        boolean result = strategy.matches(task, "Not a TaskPriority");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when criteria is null")
    void testReturnsFalseWhenCriteriaIsNull() {
        // Arrange
        task.setTaskPriority(TaskPriority.HIGH);

        // Act
        boolean result = strategy.matches(task, null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should match all TaskPriority values")
    void testMatchesAllTaskPriorityValues() {
        // Test all enum values
        for (TaskPriority priority : TaskPriority.values()) {
            // Arrange
            task.setTaskPriority(priority);

            // Act
            boolean result = strategy.matches(task, priority);

            // Assert
            assertTrue(result, "Should match for priority: " + priority);
        }
    }

    @Test
    @DisplayName("Should handle task priority set to null")
    void testHandlesTaskPriorityNull() {
        // Arrange
        task.setTaskPriority(null);

        // Act
        boolean result = strategy.matches(task, TaskPriority.HIGH);

        // Assert
        assertFalse(result);
    }
}
