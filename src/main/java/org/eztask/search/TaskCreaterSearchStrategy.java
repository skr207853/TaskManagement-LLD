package org.eztask.search;

import org.eztask.entity.Task;
import org.eztask.entity.User;

public class TaskCreaterSearchStrategy implements TaskSearchStrategy {

    @Override
    public boolean matches(Task task, Object criteria) {
        if (!(criteria instanceof User)) return false;
        User creater = (User) criteria;
        return task.getCreater() != null 
            && task.getCreater().getName() != null 
            && task.getCreater().getName().equals(creater.getName());
    }
}
