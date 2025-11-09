package org.eztask;

import org.eztask.entity.Task;
import org.eztask.entity.User;
import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskPrinter Tests")
class TaskPrinterTest {

    private TaskPrinter printer;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        printer = new TaskPrinter();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should print single task")
    void testPrintSingleTask() {
        // Arrange
        User creator = new User("Creator");
        Task task = new Task("Test Task", "Description", creator);
        List<Task> tasks = Collections.singletonList(task);

        // Act
        printer.printTasks(tasks);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Test Task"));
        assertTrue(output.contains("Description"));
        assertTrue(output.contains("Creator"));
    }

    @Test
    @DisplayName("Should print multiple tasks")
    void testPrintMultipleTasks() {
        // Arrange
        User creator = new User("Creator");
        Task task1 = new Task("Task 1", "Description 1", creator);
        Task task2 = new Task("Task 2", "Description 2", creator);
        Task task3 = new Task("Task 3", "Description 3", creator);
        List<Task> tasks = Arrays.asList(task1, task2, task3);

        // Act
        printer.printTasks(tasks);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Task 1"));
        assertTrue(output.contains("Task 2"));
        assertTrue(output.contains("Task 3"));
    }

    @Test
    @DisplayName("Should print empty list without error")
    void testPrintEmptyList() {
        // Arrange
        List<Task> tasks = Collections.emptyList();

        // Act & Assert
        assertDoesNotThrow(() -> printer.printTasks(tasks));
        String output = outContent.toString();
        assertTrue(output.isEmpty());
    }

    @Test
    @DisplayName("Should print task with all properties")
    void testPrintTaskWithAllProperties() {
        // Arrange
        User creator = new User("Creator");
        User assignee = new User("Assignee");
        Task task = new Task("Complete Task", "Full description", creator);
        task.setAssignee(assignee);
        task.setTaskStatus(TaskStatus.DEV_IN_PROGRESS);
        task.setTaskPriority(TaskPriority.HIGH);
        List<Task> tasks = Collections.singletonList(task);

        // Act
        printer.printTasks(tasks);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Complete Task"));
        assertTrue(output.contains("Full description"));
        assertTrue(output.contains("Creator"));
        assertTrue(output.contains("Assignee"));
        assertTrue(output.contains("DEV_IN_PROGRESS"));
        assertTrue(output.contains("HIGH"));
    }

    @Test
    @DisplayName("Should print task with partial properties")
    void testPrintTaskWithPartialProperties() {
        // Arrange
        User creator = new User("Creator");
        Task task = new Task("Partial Task", "Description", creator);
        // No assignee, status, or priority set
        List<Task> tasks = Collections.singletonList(task);

        // Act
        printer.printTasks(tasks);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Partial Task"));
        assertTrue(output.contains("Description"));
    }

    @Test
    @DisplayName("Should handle tasks with null assignee gracefully")
    void testPrintTaskWithNullAssignee() {
        // Arrange
        User creator = new User("Creator");
        Task task = new Task("Task", "Description", creator);
        task.setAssignee(null);
        List<Task> tasks = Collections.singletonList(task);

        // Act & Assert
        assertDoesNotThrow(() -> printer.printTasks(tasks));
    }

    @Test
    @DisplayName("Should print each task on new line")
    void testPrintTasksOnSeparateLines() {
        // Arrange
        User creator = new User("Creator");
        Task task1 = new Task("Task 1", "Desc 1", creator);
        Task task2 = new Task("Task 2", "Desc 2", creator);
        List<Task> tasks = Arrays.asList(task1, task2);

        // Act
        printer.printTasks(tasks);

        // Assert
        String output = outContent.toString();
        String[] lines = output.split(System.lineSeparator());
        assertTrue(lines.length >= 2);
    }

    @Test
    @DisplayName("Should use task toString method")
    void testUsesTaskToString() {
        // Arrange
        User creator = new User("Creator");
        Task task = new Task("Task", "Description", creator);
        List<Task> tasks = Collections.singletonList(task);

        // Act
        printer.printTasks(tasks);

        // Assert
        String output = outContent.toString();
        String expectedOutput = task.toString();
        assertTrue(output.contains(expectedOutput));
    }
}
