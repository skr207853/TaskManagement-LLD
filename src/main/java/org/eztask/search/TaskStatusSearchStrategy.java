package org.eztask.search;

import org.eztask.entity.Task;
import org.eztask.enums.TaskStatus;

public class TaskStatusSearchStrategy implements TaskSearchStrategy {
    @Override
    public boolean matches(Task task, Object criteria) {
        if (!(criteria instanceof TaskStatus)) return false;
        TaskStatus taskStatus = (TaskStatus) criteria;
        return task.getTaskStatus() == taskStatus;
    }
}
