package org.eztask.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {

    @Test
    @DisplayName("Should create user with valid name")
    void testUserCreation() {
        // Arrange & Act
        User user = new User("John Doe");

        // Assert
        assertNotNull(user);
        assertEquals("John Doe", user.getName());
    }

    @Test
    @DisplayName("Should create user with null name")
    void testUserCreationWithNullName() {
        // Arrange & Act
        User user = new User(null);

        // Assert
        assertNotNull(user);
        assertNull(user.getName());
    }

    @Test
    @DisplayName("Should create user with empty name")
    void testUserCreationWithEmptyName() {
        // Arrange & Act
        User user = new User("");

        // Assert
        assertNotNull(user);
        assertEquals("", user.getName());
    }

    @Test
    @DisplayName("Should create different users with different identities")
    void testMultipleUserCreation() {
        // Arrange & Act
        User user1 = new User("Alice");
        User user2 = new User("Bob");

        // Assert
        assertNotNull(user1);
        assertNotNull(user2);
        assertEquals("Alice", user1.getName());
        assertEquals("Bob", user2.getName());
        assertNotEquals(user1, user2);
    }
}
