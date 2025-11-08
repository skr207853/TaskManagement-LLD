package org.eztask.search;

import org.eztask.entity.Task;

public interface TaskSearchStrategy {
    boolean matches(Task task, Object criteria);
}
