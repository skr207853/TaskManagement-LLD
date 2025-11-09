package org.eztask.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Admin Entity Tests")
class AdminTest {

    @Test
    @DisplayName("Should create admin with valid name")
    void testAdminCreation() {
        // Arrange & Act
        Admin admin = new Admin("Admin User");

        // Assert
        assertNotNull(admin);
        assertEquals("Admin User", admin.getName());
    }

    @Test
    @DisplayName("Should create admin with null name")
    void testAdminCreationWithNullName() {
        // Arrange & Act
        Admin admin = new Admin(null);

        // Assert
        assertNotNull(admin);
        assertNull(admin.getName());
    }

    @Test
    @DisplayName("Should create admin with empty name")
    void testAdminCreationWithEmptyName() {
        // Arrange & Act
        Admin admin = new Admin("");

        // Assert
        assertNotNull(admin);
        assertEquals("", admin.getName());
    }

    @Test
    @DisplayName("Admin should extend User")
    void testAdminExtendsUser() {
        // Arrange & Act
        Admin admin = new Admin("Admin");

        // Assert
        assertTrue(admin instanceof User);
        assertTrue(admin instanceof Admin);
    }

    @Test
    @DisplayName("Admin should have same behavior as User")
    void testAdminBehaviorAsUser() {
        // Arrange
        Admin admin = new Admin("AdminName");
        User user = new User("UserName");

        // Act & Assert
        assertNotNull(admin.getName());
        assertNotNull(user.getName());
        assertEquals("AdminName", admin.getName());
        assertEquals("UserName", user.getName());
    }

    @Test
    @DisplayName("Should create multiple admins with different names")
    void testMultipleAdminCreation() {
        // Arrange & Act
        Admin admin1 = new Admin("Admin1");
        Admin admin2 = new Admin("Admin2");

        // Assert
        assertNotNull(admin1);
        assertNotNull(admin2);
        assertEquals("Admin1", admin1.getName());
        assertEquals("Admin2", admin2.getName());
        assertNotEquals(admin1, admin2);
    }

    @Test
    @DisplayName("Admin can be assigned to User reference")
    void testAdminAsUserReference() {
        // Arrange
        Admin admin = new Admin("Admin");

        // Act
        User userReference = admin;

        // Assert
        assertEquals("Admin", userReference.getName());
        assertTrue(userReference instanceof Admin);
    }

    @Test
    @DisplayName("Admin inherits User properties")
    void testAdminInheritsUserProperties() {
        // Arrange & Act
        Admin admin = new Admin("SuperAdmin");

        // Assert
        assertNotNull(admin.getName());
        assertEquals("SuperAdmin", admin.getName());
    }
}
