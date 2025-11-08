package org.eztask.search;

import org.eztask.entity.Comment;
import org.eztask.entity.Task;

import java.util.List;

public class TaskSearcher {
    private TaskSearchStrategy strategy;

    public TaskSearcher(TaskSearchStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Task> search(List<Task> tasks, Object criteria) {
        List<Task> result = tasks.stream().filter(task -> strategy.matches(task, criteria)).toList();
        return result;
    }
}
