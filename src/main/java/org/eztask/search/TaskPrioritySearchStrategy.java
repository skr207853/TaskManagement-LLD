package org.eztask.search;

import org.eztask.entity.Task;
import org.eztask.enums.TaskPriority;

public class TaskPrioritySearchStrategy implements TaskSearchStrategy {

    @Override
    public boolean matches(Task task, Object criteria) {
        if (!(criteria instanceof TaskPriority)) return false;
        TaskPriority taskPriority = (TaskPriority) criteria;
        return task.getTaskPriority() == taskPriority;
    }
}
