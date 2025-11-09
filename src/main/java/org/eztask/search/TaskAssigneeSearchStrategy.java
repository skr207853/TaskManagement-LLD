package org.eztask.search;

import org.eztask.entity.Task;
import org.eztask.entity.User;

public class TaskAssigneeSearchStrategy implements TaskSearchStrategy {
    @Override
    public boolean matches(Task task, Object criteria) {
        if (!(criteria instanceof User)) return false;
        User assignee = (User) criteria;
        return task.getAssignee() != null 
            && task.getAssignee().getName() != null 
            && task.getAssignee().getName().equals(assignee.getName());
    }
}
