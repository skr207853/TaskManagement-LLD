package org.eztask.search;

import org.eztask.entity.Task;
import org.eztask.entity.User;
import org.eztask.enums.TaskPriority;
import org.eztask.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TaskSearcher Tests")
class TaskSearcherTest {

    @Mock
    private TaskSearchStrategy mockStrategy;

    private TaskSearcher searcher;
    private User creator;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        creator = new User("Creator");
        task1 = new Task("Task 1", "Description 1", creator);
        task2 = new Task("Task 2", "Description 2", creator);
        task3 = new Task("Task 3", "Description 3", creator);
    }

    @Test
    @DisplayName("Should search with provided strategy")
    void testSearchWithStrategy() {
        // Arrange
        when(mockStrategy.matches(task1, "criteria")).thenReturn(true);
        when(mockStrategy.matches(task2, "criteria")).thenReturn(false);
        when(mockStrategy.matches(task3, "criteria")).thenReturn(true);

        searcher = new TaskSearcher(mockStrategy);
        List<Task> tasks = Arrays.asList(task1, task2, task3);

        // Act
        List<Task> result = searcher.search(tasks, "criteria");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task3));
        assertFalse(result.contains(task2));

        verify(mockStrategy, times(1)).matches(task1, "criteria");
        verify(mockStrategy, times(1)).matches(task2, "criteria");
        verify(mockStrategy, times(1)).matches(task3, "criteria");
    }

    @Test
    @DisplayName("Should return empty list when no matches")
    void testSearchReturnsEmptyListWhenNoMatches() {
        // Arrange
        when(mockStrategy.matches(any(), any())).thenReturn(false);

        searcher = new TaskSearcher(mockStrategy);
        List<Task> tasks = Arrays.asList(task1, task2, task3);

        // Act
        List<Task> result = searcher.search(tasks, "criteria");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return all tasks when all match")
    void testSearchReturnsAllTasksWhenAllMatch() {
        // Arrange
        when(mockStrategy.matches(any(), any())).thenReturn(true);

        searcher = new TaskSearcher(mockStrategy);
        List<Task> tasks = Arrays.asList(task1, task2, task3);

        // Act
        List<Task> result = searcher.search(tasks, "criteria");

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task2));
        assertTrue(result.contains(task3));
    }

    @Test
    @DisplayName("Should handle empty task list")
    void testSearchWithEmptyTaskList() {
        // Arrange
        searcher = new TaskSearcher(mockStrategy);
        List<Task> tasks = Arrays.asList();

        // Act
        List<Task> result = searcher.search(tasks, "criteria");

        // Assert
        assertTrue(result.isEmpty());
        verify(mockStrategy, never()).matches(any(), any());
    }

    @Test
    @DisplayName("Should search by assignee using real strategy")
    void testSearchByAssigneeWithRealStrategy() {
        // Arrange
        User assignee1 = new User("Alice");
        User assignee2 = new User("Bob");

        task1.setAssignee(assignee1);
        task2.setAssignee(assignee2);
        task3.setAssignee(assignee1);

        searcher = new TaskSearcher(new TaskAssigneeSearchStrategy());
        List<Task> tasks = Arrays.asList(task1, task2, task3);

        // Act
        List<Task> result = searcher.search(tasks, assignee1);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task3));
    }

    @Test
    @DisplayName("Should search by creator using real strategy")
    void testSearchByCreatorWithRealStrategy() {
        // Arrange
        User creator1 = new User("Creator1");
        User creator2 = new User("Creator2");

        Task task1 = new Task("Task 1", "Desc 1", creator1);
        Task task2 = new Task("Task 2", "Desc 2", creator2);
        Task task3 = new Task("Task 3", "Desc 3", creator1);

        searcher = new TaskSearcher(new TaskCreaterSearchStrategy());
        List<Task> tasks = Arrays.asList(task1, task2, task3);

        // Act
        List<Task> result = searcher.search(tasks, creator1);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task3));
    }

    @Test
    @DisplayName("Should search by status using real strategy")
    void testSearchByStatusWithRealStrategy() {
        // Arrange
        task1.setTaskStatus(TaskStatus.DEV_IN_PROGRESS);
        task2.setTaskStatus(TaskStatus.NOT_PICKED);
        task3.setTaskStatus(TaskStatus.DEV_IN_PROGRESS);

        searcher = new TaskSearcher(new TaskStatusSearchStrategy());
        List<Task> tasks = Arrays.asList(task1, task2, task3);

        // Act
        List<Task> result = searcher.search(tasks, TaskStatus.DEV_IN_PROGRESS);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task3));
    }

    @Test
    @DisplayName("Should search by priority using real strategy")
    void testSearchByPriorityWithRealStrategy() {
        // Arrange
        task1.setTaskPriority(TaskPriority.HIGH);
        task2.setTaskPriority(TaskPriority.LOW);
        task3.setTaskPriority(TaskPriority.HIGH);

        searcher = new TaskSearcher(new TaskPrioritySearchStrategy());
        List<Task> tasks = Arrays.asList(task1, task2, task3);

        // Act
        List<Task> result = searcher.search(tasks, TaskPriority.HIGH);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task3));
    }
}
