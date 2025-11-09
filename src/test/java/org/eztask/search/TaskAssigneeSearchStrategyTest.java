package org.eztask.search;

import org.eztask.entity.Task;
import org.eztask.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskAssigneeSearchStrategy Tests")
class TaskAssigneeSearchStrategyTest {

    private TaskAssigneeSearchStrategy strategy;
    private User creator;
    private User assignee1;
    private User assignee2;
    private Task task;

    @BeforeEach
    void setUp() {
        strategy = new TaskAssigneeSearchStrategy();
        creator = new User("Creator");
        assignee1 = new User("Alice");
        assignee2 = new User("Bob");
        task = new Task("Test Task", "Description", creator);
    }

    @Test
    @DisplayName("Should match task with assigned user")
    void testMatchesWithAssignedUser() {
        // Arrange
        task.setAssignee(assignee1);

        // Act
        boolean result = strategy.matches(task, assignee1);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should not match task with different assignee")
    void testDoesNotMatchWithDifferentAssignee() {
        // Arrange
        task.setAssignee(assignee1);

        // Act
        boolean result = strategy.matches(task, assignee2);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should not match task with no assignee")
    void testDoesNotMatchTaskWithNoAssignee() {
        // Arrange - task has no assignee

        // Act
        boolean result = strategy.matches(task, assignee1);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when criteria is not a User")
    void testReturnsFalseWhenCriteriaIsNotUser() {
        // Arrange
        task.setAssignee(assignee1);

        // Act
        boolean result = strategy.matches(task, "Not a User");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when criteria is null")
    void testReturnsFalseWhenCriteriaIsNull() {
        // Arrange
        task.setAssignee(assignee1);

        // Act
        boolean result = strategy.matches(task, null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle null assignee name")
    void testHandlesNullAssigneeName() {
        // Arrange
        User userWithNullName = new User(null);
        task.setAssignee(userWithNullName);

        // Act
        boolean result = strategy.matches(task, assignee1);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should match users with same name")
    void testMatchesUsersWithSameName() {
        // Arrange
        User assignee1Copy = new User("Alice");
        task.setAssignee(assignee1);

        // Act
        boolean result = strategy.matches(task, assignee1Copy);

        // Assert
        assertTrue(result);
    }
}
