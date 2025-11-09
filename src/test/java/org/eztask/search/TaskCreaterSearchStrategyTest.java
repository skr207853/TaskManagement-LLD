package org.eztask.search;

import org.eztask.entity.Task;
import org.eztask.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskCreaterSearchStrategy Tests")
class TaskCreaterSearchStrategyTest {

    private TaskCreaterSearchStrategy strategy;
    private User creator1;
    private User creator2;
    private Task task;

    @BeforeEach
    void setUp() {
        strategy = new TaskCreaterSearchStrategy();
        creator1 = new User("Alice");
        creator2 = new User("Bob");
        task = new Task("Test Task", "Description", creator1);
    }

    @Test
    @DisplayName("Should match task with creator")
    void testMatchesWithCreator() {
        // Act
        boolean result = strategy.matches(task, creator1);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should not match task with different creator")
    void testDoesNotMatchWithDifferentCreator() {
        // Act
        boolean result = strategy.matches(task, creator2);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when criteria is not a User")
    void testReturnsFalseWhenCriteriaIsNotUser() {
        // Act
        boolean result = strategy.matches(task, "Not a User");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when criteria is null")
    void testReturnsFalseWhenCriteriaIsNull() {
        // Act
        boolean result = strategy.matches(task, null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should match users with same name")
    void testMatchesUsersWithSameName() {
        // Arrange
        User creator1Copy = new User("Alice");

        // Act
        boolean result = strategy.matches(task, creator1Copy);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should handle null creator name")
    void testHandlesNullCreatorName() {
        // Arrange
        User creatorWithNullName = new User(null);
        Task taskWithNullCreatorName = new Task("Task", "Desc", creatorWithNullName);

        // Act
        boolean result = strategy.matches(taskWithNullCreatorName, creator1);

        // Assert
        assertFalse(result);
    }
}
