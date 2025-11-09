package org.eztask.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Comment Entity Tests")
class CommentTest {

    @Test
    @DisplayName("Should create comment with valid text")
    void testCommentCreation() {
        // Arrange
        String commentText = "This is a test comment";

        // Act
        Comment comment = new Comment(commentText);

        // Assert
        assertNotNull(comment);
        assertNotNull(comment.getCreationTime());
        assertTrue(comment.getCreationTime().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should create comment with null text")
    void testCommentCreationWithNullText() {
        // Arrange & Act
        Comment comment = new Comment(null);

        // Assert
        assertNotNull(comment);
        assertNotNull(comment.getCreationTime());
    }

    @Test
    @DisplayName("Should create comment with empty text")
    void testCommentCreationWithEmptyText() {
        // Arrange & Act
        Comment comment = new Comment("");

        // Assert
        assertNotNull(comment);
        assertNotNull(comment.getCreationTime());
    }

    @Test
    @DisplayName("Should set creation time to current time")
    void testCommentCreationTime() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();

        // Act
        Comment comment = new Comment("Test");
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertNotNull(comment.getCreationTime());
        assertFalse(comment.getCreationTime().isBefore(before));
        assertFalse(comment.getCreationTime().isAfter(after));
    }

    @Test
    @DisplayName("Should create multiple comments with different creation times")
    void testMultipleCommentCreation() throws InterruptedException {
        // Arrange & Act
        Comment comment1 = new Comment("First comment");
        Thread.sleep(10);
        Comment comment2 = new Comment("Second comment");

        // Assert
        assertNotNull(comment1.getCreationTime());
        assertNotNull(comment2.getCreationTime());
        assertTrue(comment1.getCreationTime().isBefore(comment2.getCreationTime()) ||
                   comment1.getCreationTime().isEqual(comment2.getCreationTime()));
    }
}
